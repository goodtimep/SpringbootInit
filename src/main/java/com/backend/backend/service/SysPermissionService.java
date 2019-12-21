package com.backend.backend.service;

import com.backend.backend.model.entity.sys.SysPermission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Author: goodtimp
 * @Date: 2019/11/8 9:09
 * @description :
 */
public interface SysPermissionService extends IService<SysPermission> {
    void addPermissionWithRole(SysPermission sysPermission);

//    void saveOrUpdatePermissionForRedis(SysPermission sysPermission);

    void updatePermissionForRole(SysPermission sysPermission);

    void updatePermissionByCode(SysPermission sysPermission);

    /**
     * 删除某个权限 逻辑删除
     *
     * @param perId
     */
    void deleteById(Long perId);

    /**
     * 得到所有的权限信息
     *
     * @return
     */
    List<SysPermission> getAllPermission();


    /**
     * 初始化redis中权限数据
     */
    void initPermissionForRedis();

    /**
     * 得到redis中的所有权限信息
     *
     * @return
     */
    List<SysPermission> getAllPermissionRedis();

    /**
     * 通过id得到permission信息
     *
     * @return
     */
    List<SysPermission> getPermissionByIds(List<Long> ids);

    /**
     * 从redis中通过id得到permission信息
     *
     * @param ids
     * @return
     */
    List<SysPermission> getPermissionByFromRedis(List<Long> ids);

    /**
     * 得到当前登录用户的permission
     *
     * @return 如果未登录返回null
     */
    List<SysPermission> getCurrentPermission();

    /**
     * 通过code删除某个权限
     *
     * @param code
     * @return
     */
    boolean deleteByCode(String code);
}
