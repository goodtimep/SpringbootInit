package com.backend.backend.service.impl;

import com.backend.backend.dao.SysRoleMapper;
import com.backend.backend.enums.DelFlagEnum;
import com.backend.backend.enums.RedisEnum;
import com.backend.backend.model.entity.sys.SysPermission;
import com.backend.backend.model.entity.sys.SysRole;
import com.backend.backend.redis.RedisUtil;
import com.backend.backend.service.SysRolePermissionRelationService;
import com.backend.backend.service.SysRoleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: goodtimp
 * @Date: 2019/11/8 9:12
 * @description :
 */
@Service
@RequiredArgsConstructor(onConstructor = @_(@Autowired))
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    final private SysRoleMapper sysRoleMapper;
    final private SysRolePermissionRelationService sysRolePermissionRelationServiceImpl;

    @Override
    public List<SysRole> getRoleIdsByIds(List<Long> roleIds) {
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(roleIds != null, SysRole::getRoleId, roleIds)
                .eq(SysRole::getDelFlag, DelFlagEnum.NORMAL.getCode())
                .orderBy(true, false, SysRole::getCreateTime);
        return sysRoleMapper.selectList(queryWrapper);
    }

    @Override
    public List<SysRole> getRoleIdsByIdsForRedis(List<Long> roleIds) {
        Map<Object, Object> map = RedisUtil.hmget(RedisEnum.ROLE_HASH_MAP.getCode());
        List<SysRole> list = new ArrayList<>();
        if (roleIds != null)
            roleIds.forEach(e -> list.add((SysRole) map.get(e.toString())));
        else
            for (Map.Entry<Object, Object> i : map.entrySet()) {
                list.add((SysRole) i.getValue());
            }
        return list;
    }

    @Override
    public void updateRole(SysRole sysRole) {
        sysRole.setUpdate();
        this.updateById(sysRole);
        initRoleSqlForRedis();
    }

    @Override
    public void addRole(SysRole sysRole) {
        sysRole.setCreate();
        this.save(sysRole);
        initRoleSqlForRedis();
    }


    @Override
    public void deleteById(Long roleId) {
        SysRole sysRole = new SysRole();
        sysRole.setRoleId(roleId);
        sysRole.delete();
        sysRoleMapper.updateById(sysRole);
        sysRolePermissionRelationServiceImpl.deletePermissionWithRole(roleId, null);
        initRoleSqlForRedis();
    }

    @Override
    public void initRoleSqlForRedis() {
        List<SysRole> sysRoles = getRoleIdsByIds(null);
        HashMap<String, Object> map = new HashMap<String, Object>();
        sysRoles.stream().forEach(e -> map.put(e.getRoleId().toString(), e));
        RedisUtil.hmset(RedisEnum.ROLE_HASH_MAP.getCode(), map);
    }
}
