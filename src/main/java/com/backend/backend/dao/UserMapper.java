package com.backend.backend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backend.backend.model.entity.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: goodtimp
 * @Date: 2019/10/1 13:14
 * @description :
 */

public interface UserMapper extends BaseMapper<User> {
}
