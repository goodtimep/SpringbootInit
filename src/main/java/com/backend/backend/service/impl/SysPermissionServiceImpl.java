package com.backend.backend.service.impl;

import com.backend.backend.dao.SysPermissionMapper;
import com.backend.backend.enums.DelFlagEnum;
import com.backend.backend.enums.RedisEnum;
import com.backend.backend.jwt.JwtUtil;
import com.backend.backend.model.entity.User;
import com.backend.backend.model.entity.relation.SysRolePermissionRelation;
import com.backend.backend.model.entity.sys.SysPermission;
import com.backend.backend.redis.RedisUtil;
import com.backend.backend.service.SysPermissionService;
import com.backend.backend.service.SysRolePermissionRelationService;
import com.backend.backend.service.SysRoleService;
import com.backend.backend.service.SysUserRoleRelationService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: goodtimp
 * @Date: 2019/11/8 9:10
 * @description :
 */
@Service
@RequiredArgsConstructor(onConstructor = @_(@Autowired))
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {
    private final SysPermissionMapper sysPermissionMapper;
    private final SysRolePermissionRelationService sysRolePermissionRelationServiceImpl;
    final private SysRoleService sysRoleServiceImpl;
    final private SysUserRoleRelationService sysUserRoleRelationServiceImpl;

    /**
     * 新增
     *
     * @param sysPermission
     */
    @Override
    public void addPermissionWithRole(SysPermission sysPermission) {
        sysPermission.setCreate();
        save(sysPermission);

        // 修改redis中内容
        saveOrUpdatePermissionForRedis(sysPermission);
    }


    /**
     * 修改
     *
     * @param sysPermission
     */
    @Override
    public void updatePermissionForRole(SysPermission sysPermission) {
        sysPermission.setUpdate();
        updateById(sysPermission);
        // 修改redis中内容
        saveOrUpdatePermissionForRedis(sysPermission);
    }

    /**
     * 根据PermissionCode进行修改,sysPermission为引用变量将添加Id信息
     *
     * @param sysPermission
     */
    @Override
    public void updatePermissionByCode(SysPermission sysPermission) {
        // 获取id等信息
        QueryWrapper<SysPermission> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().select(SysPermission::getPerId).
                eq(SysPermission::getPerCode, sysPermission.getPerCode()).
                eq(SysPermission::getDelFlag, DelFlagEnum.NORMAL.getCode());
        sysPermission.setPerId(sysPermissionMapper.selectOne(queryWrapper).getPerId());
        sysPermission.setUpdate();
        //修改
        updateById(sysPermission);
        // 修改redis中内容
        saveOrUpdatePermissionForRedis(sysPermission);
    }

    @Override
    public void deleteById(Long perId) {
        SysPermission sysPermission = new SysPermission();
        sysPermission.setPerId(perId);
        sysPermission.delete();
        this.updateById(sysPermission);
        deletePermissionWithRoleByIdForRedis(perId);
    }

    @Override
    public List<SysPermission> getAllPermission() {
        QueryWrapper<SysPermission> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(SysPermission::getDelFlag, DelFlagEnum.NORMAL.getCode())
                .eq(SysPermission::getDelFlag, DelFlagEnum.NORMAL.getCode())
                .orderBy(true, false, SysPermission::getPerCode);
        return sysPermissionMapper.selectList(queryWrapper);
    }

    @Override
    public void initPermissionForRedis() {
        List<SysPermission> list = getAllPermission();
        HashMap<String, Object> map = new HashMap<String, Object>();
        list.stream().forEach(e -> map.put(e.getPerId().toString(), e));
        RedisUtil.hmset(RedisEnum.PERMISSION_HASH_MAP.getCode(), map);
    }

    /**
     * 得到redis中所有的权限信息
     *
     * @return
     */
    public List<SysPermission> getAllPermissionRedis() {
        List<SysPermission> list = new ArrayList<>();
        Map<Object, Object> per = RedisUtil.hmget(RedisEnum.PERMISSION_HASH_MAP.getCode());
        for (Map.Entry<Object, Object> i : per.entrySet()) {
            list.add((SysPermission) i.getValue());
        }
        return list;
    }

    /**
     * 新增或修改 redis
     *
     * @param sysPermission
     */
//    @Override
    public void saveOrUpdatePermissionForRedis(SysPermission sysPermission) {
        // 修改redis中内容
        RedisUtil.hset(RedisEnum.PERMISSION_HASH_MAP.getCode(), sysPermission.getPerId().toString(), sysPermission);
    }

    /**
     * 删除 redis
     *
     * @param
     */
//    @Override
    public void deletePermissionWithRoleByIdForRedis(Object... perIds) {
        // 修改redis中内容
        RedisUtil.hdel(RedisEnum.PERMISSION_HASH_MAP.getCode(), perIds);
    }

    /**
     * 通过id得到permission信息
     *
     * @return
     */
    @Override
    public List<SysPermission> getPermissionByIds(List<Long> ids) {
        QueryWrapper<SysPermission> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(SysPermission::getDelFlag, DelFlagEnum.NORMAL.getCode()).in(SysPermission::getPerId, ids)
                .orderBy(true, false, SysPermission::getCreateTime);
        return sysPermissionMapper.selectList(queryWrapper);
    }

    /**
     * 从redis中通过id得到permission信息
     *
     * @param ids 传入的permission Id列表
     * @return
     */
    @Override
    public List<SysPermission> getPermissionByFromRedis(List<Long> ids) {
        List<SysPermission> result = new ArrayList<>();
        ids.stream().forEach(e ->
        {
            Object temp = RedisUtil.hget(RedisEnum.PERMISSION_HASH_MAP.getCode(), e.toString());
            if (temp != null) result.add((SysPermission) temp);
        });
        return result;
    }


    /**
     * 得到当前登录用户的permission
     *
     * @return 如果未登录返回null
     */
    public List<SysPermission> getCurrentPermission() {
        User user = JwtUtil.getCurrentUserOfToken();
        List<Long> roleIds = sysUserRoleRelationServiceImpl.getRoleIdsByUserId(user.getUserId());
        List<SysRolePermissionRelation> permissions = sysRolePermissionRelationServiceImpl.
                getPermissionIdsByRoleIdsForRedis(roleIds);
        List<SysPermission> list = getPermissionByFromRedis(permissions.stream().map(e -> e.getPermissionId())
                .collect(Collectors.toList()));
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByCode(String code) {
        QueryWrapper<SysPermission> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(SysPermission::getPerCode, code).eq(SysPermission::getDelFlag, DelFlagEnum.NORMAL.getCode());
        List<SysPermission> sysPermissions = sysPermissionMapper.selectList(queryWrapper);
        sysPermissions.stream().forEach(e -> {
            e.delete();
            this.updateById(e);
            deletePermissionWithRoleByIdForRedis(e.getPerId().toString());
        });
        return true;
    }
}
