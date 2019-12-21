package com.backend.backend.service.impl;

import com.backend.backend.dao.SysRolePermissionRelationMapper;
import com.backend.backend.enums.RedisEnum;
import com.backend.backend.model.entity.relation.SysRolePermissionRelation;
import com.backend.backend.redis.RedisUtil;
import com.backend.backend.service.SysRolePermissionRelationService;
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
 * @Date: 2019/11/8 11:19
 * @description : 角色权限关联表，关联表内容查询只在redis查询。
 */
@Service
@RequiredArgsConstructor(onConstructor = @_(@Autowired))
public class SysRolePermissionRelationServiceImpl extends ServiceImpl<SysRolePermissionRelationMapper, SysRolePermissionRelation> implements SysRolePermissionRelationService {
    final private SysRolePermissionRelationMapper sysRolePermissionRelationMapper;

    /**
     * 获取角色的所有权限Id redis
     *
     * @param roleId
     * @return
     */
    @Override
    public List<SysRolePermissionRelation> getIdsByRoleId(Long roleId) {
        QueryWrapper<SysRolePermissionRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysRolePermissionRelation::getRoleId, roleId);

        return sysRolePermissionRelationMapper.selectList(queryWrapper);
    }

    /**
     * 获取多个角色的所有权限信息,传入值为null时获取所有
     *
     * @param roleIds
     * @return
     */
    @Override
    public List<SysRolePermissionRelation> getPermissionIdsByRoleIds(List<Long> roleIds) {
        QueryWrapper<SysRolePermissionRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(roleIds != null, SysRolePermissionRelation::getRoleId, roleIds);

        return sysRolePermissionRelationMapper.selectList(queryWrapper);
    }

    @Override
    public void addPermissionWithRole(List<SysRolePermissionRelation> list) {
        list = list.stream().filter(e -> (!e.getPermissionId().equals(0L))).collect(Collectors.toList());
        if (!list.isEmpty()) {
            this.saveBatch(list);
            initPermissionRoleRelation();
        }
    }

    @Override
    public void deletePermissionWithRole(Long roleId, List<Long> permissionIds) {
        QueryWrapper<SysRolePermissionRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysRolePermissionRelation::getRoleId, roleId)
                .in(permissionIds != null, SysRolePermissionRelation::getPermissionId, permissionIds);
        this.remove(queryWrapper);
        initPermissionRoleRelation();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePermissionForRole(List<SysRolePermissionRelation> list) {
        if (list == null || list.isEmpty()) return;
        // 删除角色所有权限
        deletePermissionWithRole(list.get(0).getRoleId(), null);
        // 增加角色信息
        addPermissionWithRole(list);
        initPermissionRoleRelation();
    }

    /**
     * 初始化权限角色对应表, key:map({"roleId":"perId0,perId2"})
     */
    @Override
    public void initPermissionRoleRelation() {
        List<SysRolePermissionRelation> list = getPermissionIdsByRoleIds(null);
        HashMap<String, Object> map = new HashMap<String, Object>();
        list.stream().forEach(e ->
        {
            Object temp = map.get(e.getRoleId().toString());
            map.put(e.getRoleId().toString(), (temp == null ? "" : ((String) temp)) + "," + e.getPermissionId().toString());
        });
        RedisUtil.hmset(RedisEnum.ROLE_PERMISSION_RELATION_HASH_MAP.getCode(), map);
    }

    /**
     * 从Reids中获取多个角色的所有权限信息,传入值为null时获取所有
     *
     * @param roleIds
     * @return
     */
    @Override
    public List<SysRolePermissionRelation> getPermissionIdsByRoleIdsForRedis(List<Long> roleIds) {
        List<SysRolePermissionRelation> list = new ArrayList<>();
        // 获取所有
        if (roleIds == null) {
            Map<Object, Object> map = RedisUtil.hmget(RedisEnum.ROLE_PERMISSION_RELATION_HASH_MAP.getCode());
            for (Map.Entry<Object, Object> i : map.entrySet()) {
                list.add(new SysRolePermissionRelation((Long) i.getKey(), (Long) i.getValue()));
            }
        }
        if (!roleIds.isEmpty()) {
            // 获取roleIds对应的
            roleIds.stream().forEach(e -> {
                Object temp = RedisUtil.hget(RedisEnum.ROLE_PERMISSION_RELATION_HASH_MAP.getCode(), e.toString());
                if (temp != null) {
                    String[] ids = ((String) temp).split(",");
                    for (String id : ids) {
                        if (!id.equals(""))
                            list.add(new SysRolePermissionRelation(Long.parseLong(id), e));
                    }
                }
            });
        }
        return list;
    }

//    /**
//     * 去除与redis（数据库）中重复的对应关系
//     *
//     * @param list
//     * @return
//     */
//    private List<SysRolePermissionRelation> removeDuplication(List<SysRolePermissionRelation> list) {
//        getIdsByRoleId()
//    }
}
