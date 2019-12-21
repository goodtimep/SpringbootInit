package com.backend.backend.dao;

import com.backend.backend.model.entity.sys.SysDict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: goodtimp
 * @Date: 2019/11/18 15:10
 * @description :
 */
public interface SysDictMapper extends BaseMapper<SysDict> {
    /**
     * 更新orderNumber
     */
    void updateOrderNumber();
}
