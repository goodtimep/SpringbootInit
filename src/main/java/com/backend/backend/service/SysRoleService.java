package com.backend.backend.service;

import com.backend.backend.model.entity.sys.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author: goodtimp
 * @Date: 2019/11/8 9:06
 * @description :  角色
 */
public interface SysRoleService extends IService<SysRole> {
    /**
     * 得到角色
     *
     * @param roleId
     * @return
     */
    List<SysRole> getRoleIdsByIds(List<Long> roleId);

    /**
     * 从redis中获取roleIds，如果传入为null则获取所有
     *
     * @param roleIds
     * @return
     */
    List<SysRole> getRoleIdsByIdsForRedis(List<Long> roleIds);

    /**
     * 更新，默认更新redis
     *
     * @param sysRole
     */
    void updateRole(SysRole sysRole);

    /**
     * 添加,默认更新redis
     *
     * @param sysRole
     */
    void addRole(SysRole sysRole);

    /**
     * 逻辑删除，并且删除角色对应的权限信息
     *
     * @param roleId
     */
    void deleteById(Long roleId);

    /**
     * 初始化redis中的Role信息
     */
    void initRoleSqlForRedis();


}
