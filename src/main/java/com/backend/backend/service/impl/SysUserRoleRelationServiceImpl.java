package com.backend.backend.service.impl;

import com.backend.backend.dao.SysUserRoleRelationMapper;
import com.backend.backend.model.entity.relation.SysUserRoleRelation;
import com.backend.backend.service.SysUserRoleRelationService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: goodtimp
 * @Date: 2019/11/8 11:17
 * @description :
 */
@Service
@RequiredArgsConstructor(onConstructor = @_(@Autowired))
public class SysUserRoleRelationServiceImpl extends ServiceImpl<SysUserRoleRelationMapper, SysUserRoleRelation> implements SysUserRoleRelationService {
    private final SysUserRoleRelationMapper sysUserRoleRelationMapper;

    @Override
    public List<Long> getRoleIdsByUserId(Long userId) {
        QueryWrapper<SysUserRoleRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUserRoleRelation::getUserId, userId);
        return sysUserRoleRelationMapper.selectList(queryWrapper)
                .stream().map(e -> e.getRoleId()).collect(Collectors.toList());
    }

    @Override
    public List<Long> getUserIdsByRoleId(Long roleId) {
        QueryWrapper<SysUserRoleRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUserRoleRelation::getRoleId, roleId);
        return sysUserRoleRelationMapper.selectList(queryWrapper)
                .stream().map(e -> e.getUserId()).collect(Collectors.toList());
    }

    @Override
    public void deleteRoleByUserId(Long userId, List<Long> roleIds) {
        QueryWrapper<SysUserRoleRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUserRoleRelation::getUserId, userId)
                .in(roleIds != null, SysUserRoleRelation::getRoleId, roleIds);
        this.remove(queryWrapper);
    }

    @Override
    public void addRoleForUserId(List<SysUserRoleRelation> list) {
        this.saveBatch(list.stream().filter(e -> !e.getRoleId().equals(0L)).collect(Collectors.toList()));
    }

    @Override
    public void updateRoleForUserId(List<SysUserRoleRelation> list) {
        if (list == null || list.isEmpty()) return;
        deleteRoleByUserId(list.get(0).getUserId(), null);
        addRoleForUserId(list);
    }
}
