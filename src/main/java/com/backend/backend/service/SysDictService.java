package com.backend.backend.service;

import com.backend.backend.model.entity.sys.SysDict;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Author: goodtimp
 * @Date: 2019/11/18 15:00
 * @description :  系统字典类
 */
public interface SysDictService extends IService<SysDict> {

    /**
     * 通过父级Id获取所有SysDict
     *
     * @param parentId
     * @return
     */
    List<SysDict> getByParentId(Long parentId);

    /**
     * getById
     *
     * @param id
     * @return
     */
    SysDict getByDictId(Long id);

    /**
     * 增加
     *
     * @param sysDict
     */
    void saveSysDict(SysDict sysDict);

    /**
     * 增加
     *
     * @param sysDict
     */
    void updateSysDict(SysDict sysDict);

    /**
     * 通过Id列表删除SysDict
     *
     * @param ids
     */
    void deleteSysDict(List<Long> ids);
}
