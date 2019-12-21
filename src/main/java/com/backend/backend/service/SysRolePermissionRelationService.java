package com.backend.backend.service;

import com.backend.backend.model.entity.relation.SysRolePermissionRelation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Author: goodtimp
 * @Date: 2019/11/8 11:16
 * @description : 角色权限关联表，关联表内容查询只在redis查询。
 */
public interface SysRolePermissionRelationService extends IService<SysRolePermissionRelation> {
    /**
     * 获取角色的所有权限Id redis
     *
     * @param roleId
     * @return
     */
    List<SysRolePermissionRelation> getIdsByRoleId(Long roleId);

    /**
     * 获取多个角色的所有权限信息
     *
     * @param roleIds
     * @return
     */
    List<SysRolePermissionRelation> getPermissionIdsByRoleIds(List<Long> roleIds);

    /**
     * 增加权限信息
     *
     * @param roleId
     * @param permissionIds
     */
    void addPermissionWithRole(List<SysRolePermissionRelation> list);


    /**
     * 删除权限信息
     *
     * @param roleId
     * @param permissionIds null或者empty时 删除所有
     */
    void deletePermissionWithRole(Long roleId, List<Long> permissionIds);

    /**
     * 更新 将角色的所有权限信息更改为传入信息
     *
     * @param list
     */
    void updatePermissionForRole(List<SysRolePermissionRelation> list);

    /**
     * 从Reids中获取多个角色的所有权限信息,传入值为null时获取所有，
     *
     * @param roleIds null时获取所有
     * @return
     */
    List<SysRolePermissionRelation> getPermissionIdsByRoleIdsForRedis(List<Long> roleIds);


    /**
     * 初始化权限列表
     */
    void initPermissionRoleRelation();
}
