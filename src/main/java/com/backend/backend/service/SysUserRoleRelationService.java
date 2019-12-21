package com.backend.backend.service;

import com.backend.backend.model.entity.relation.SysUserRoleRelation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Author: goodtimp
 * @Date: 2019/11/8 11:16
 * @description :
 */
public interface SysUserRoleRelationService extends IService<SysUserRoleRelation> {
    /**
     * 通过用户Id得到对应的所有角色ids
     *
     * @return
     * @Param userId
     */
    List<Long> getRoleIdsByUserId(Long userId);

    /**
     * 通过角色Id得到对应的所有用户
     *
     * @return
     * @Param roleId
     */
    List<Long> getUserIdsByRoleId(Long roleId);

    /**
     * 删除用户的 roleIds
     *
     * @param userId
     * @param roleIds
     */
    void deleteRoleByUserId(Long userId, List<Long> roleIds);
    /**
     * 用户添加角色
     *
     * @param userId
     * @param roleIds
     */
    void addRoleForUserId(List<SysUserRoleRelation> list);

    /**
     * 更新用户角色信息
     *
     * @param userId
     * @param roleIds
     */
    void updateRoleForUserId(List<SysUserRoleRelation> list);
}
