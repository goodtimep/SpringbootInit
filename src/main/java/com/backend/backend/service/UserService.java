package com.backend.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.backend.backend.model.entity.User;

import java.util.List;
import java.util.Map;

/**
 * @Author: goodtimp
 * @Date: 2019/9/24 19:22
 * @description :  用户管理
 */

public interface UserService extends IService<User> {

    /**
     * 通过id获取用户信息
     *
     * @param id 用户id
     * @return UserEnum
     */
    User getUserById(Long id);

    /**
     * 通过手机号/Email登录
     *
     * @param user 包含用户手机号/Email的用户信息
     * @return 成功返回User对象，失败返回null
     */
    User login(User user);

    /**
     * 通过name获取用户信息
     *
     * @param name 用户姓名
     * @return List UserEnum
     */
    List<User> getUserByName(String name);

    /**
     * 用户注册
     *
     * @param user 用户信息
     * @return 返回注册后数据，注册失败返回null
     */
    User signIn(User user);

    /**
     * 用户更新
     *
     * @param user 更新后的用户信息
     * @return UserEnum
     */
    User updateOne(User user);

    /**
     * 删除一条用户记录通过id
     *
     * @param id 用户id
     * @return Bool
     */
    Boolean deleteById(Long id);

    /**
     * change password
     *
     * @param oldPass 原密码
     * @param newPass 新密码
     * @return UserEnum
     */
    User changePassword(String oldPass, String newPass);

    /**
     * 获取当前登录用户信息
     * @return UserEnum
     */
    User getInfo();


    /**
     * 分页获取全部信息(包括管理员)
     * @param  currPage 当前页
     * @param  size     每页数量
     * @return UserEnum List
     */
    IPage<User> selectPage(Integer currPage, Integer size);

    /**
     * 获取所有用户数量
     * @return Integer
     */
    Integer getCount();

    /**
     * 通过Type值获取所有用户数量
     * @param type 用户type
     * @return Integer
     */
    Integer getCountByType(Integer type);


    /**
     * 获取用户列表
     * @param param Map
     * @return IPage
     */
    IPage<User> getList(Map<String, Object> param);
}
