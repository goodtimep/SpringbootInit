package com.backend.backend.service.impl;

import com.backend.backend.common.FormatCheck;
import com.backend.backend.exception.RestException;
import com.backend.backend.jwt.JwtUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.backend.backend.common.Tools;
import com.backend.backend.dao.UserMapper;
import com.backend.backend.enums.DelFlagEnum;
import com.backend.backend.exception.UserException;
import com.backend.backend.model.entity.User;
import com.backend.backend.service.UserService;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author ipso
 * Transactional 开启事务,注意由于数据使用的是innodb引擎，不开启事务的话，默认是每一
 * 句sql就是一个事务，就会上一次相关的操作琐，非常影响性能
 * Propagation.NESTED 嵌套事务
 * Isolation.DEFAULT  事务隔离级别使用数据库默认
 * readOnly = false   在查询的方式上设置true为只读事务：这样不加锁
 * rollbackFor        指定异常回滚处理类
 */
@Service
@Transactional(propagation = Propagation.NESTED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = RestException.class)
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 通过id获取用户信息
     *
     * @param id 用户id
     * @return UserEnum
     */
    @Transactional(readOnly = true)
    @Override
    public User getUserById(Long id) {

        // 判断是否存在用户，是否被删除
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getDelFlag, DelFlagEnum.NORMAL.getCode()).eq(User::getUserId, id);
        User user = this.getOne(queryWrapper);
        if (user == null)
            return null;

        return user;
    }


    /**
     * 根据用户手机号、Email登录
     *
     * @param user 登录的用户信息
     * @return UserEnum, 借用setDelFlag字段传递登录状态，3：账号不存在，4：密码不正确，这里对数据库中的用户信息并没有影响
     * @throws UserException e
     */
    @Override
    @Transactional(readOnly = true)
    // @Deprecated 弃用
    public User login(User user) throws UserException {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        String username = null;
        String password = user.getPassword();
        if (user.getUserPhone() != null) { // 手机登录
            username = user.getUserPhone();
            queryWrapper.lambda().eq(User::getUserPhone, username).eq(User::getDelFlag, DelFlagEnum.NORMAL.getCode());
        } else { // 电子邮箱登录
            username = user.getUserEmail();
            queryWrapper.lambda().eq(User::getUserEmail, username).eq(User::getDelFlag, DelFlagEnum.NORMAL.getCode());
        }

        User loginUser = this.getOne(queryWrapper);
        if (loginUser == null)
            loginUser.setDelFlag(3);

        String loginPassword = addSaltForPassword(password, loginUser.getSalt());
        if (!loginPassword.equals(loginUser.getPassword()))
            loginUser.setDelFlag(4);
        return loginUser;
    }

    /**
     * 根据姓名获取一条用户信息
     *
     * @param name 用户姓名
     * @return List UserEnum
     */
    @Transactional(readOnly = true)
    @Override
    public List<User> getUserByName(String name) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getName, name).eq(User::getDelFlag, DelFlagEnum.NORMAL.getCode());
        return this.list(queryWrapper);
    }

    /**
     * 用户注册
     *
     * @param user 用户信息
     * @return UserEnum
     */
    @Override
    public User signIn(User user) {

        // 判断用户手机号、邮箱以及姓名是否已将被注册
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getDelFlag, DelFlagEnum.NORMAL.getCode())
                .and(e -> e.eq(User::getUserPhone, user.getUserPhone())
                        .or().eq(User::getUserEmail, user.getUserEmail()));
        User isUser = this.getOne(queryWrapper);
        if (isUser != null) {
            return null;
        }

        user.setSalt(Tools.getRandomString(20)); // 获取长度为20的盐
        String saltPass = addSaltForPassword(user.getPassword(), user.getSalt());// shiro中加密必须要用Md5Hash
        user.setPassword(saltPass);
        // user.setPermission(1); // 普通用户注册
        user.setCreate();
        this.save(user);
        return user;
    }


    /**
     * 获取当前登录用户信息
     *
     * @return UserEnum
     */
    @Override
    public User getInfo() {
        User user = JwtUtil.getCurrentUserOfToken();

        User us = this.getById(user.getUserId());
        return us;
    }


    /**
     * 分页获取全部信息(包括管理员)
     *
     * @param currPage 当前页
     * @param size     每页数量
     * @return UserEnum List
     */
    @Override
    @Transactional(readOnly = true)
    public IPage<User> selectPage(Integer currPage, Integer size) {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getDelFlag, DelFlagEnum.NORMAL.getCode());

        Page<User> page = new Page<>(currPage, size);
        return this.page(page, queryWrapper);
    }

    /**
     * 获取所有用户数量
     *
     * @return Integer
     */
    @Override
    @Transactional(readOnly = true)
    public Integer getCount() {

        Integer count = null;
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getDelFlag, DelFlagEnum.NORMAL.getCode());

        count = this.count(queryWrapper);
        return count;
    }

    /**
     * 通过用户type获取用户记录数量
     *
     * @param type 用户type
     * @return Integer
     */
    @Override
    @Transactional(readOnly = true)
    public Integer getCountByType(Integer type) {

        Integer count = null;
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getDelFlag, DelFlagEnum.NORMAL.getCode()).eq(User::getType, type);

        count = this.count(queryWrapper);
        return count;
    }


    /**
     * 更新用户信息
     *
     * @param user 用户信息
     * @return UserEnum
     */
    @Override
    public User updateOne(User user) {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getDelFlag, DelFlagEnum.NORMAL.getCode()).eq(User::getUserId, user.getUserId());
        User isUser = this.getOne(queryWrapper);
        if (isUser == null) {
            return null;
        }

        user.setSalt(Tools.getRandomString(20)); // 获取长度为20的盐
        String saltPass = addSaltForPassword(user.getPassword(), user.getSalt());
        user.setPassword(saltPass);
        user.setUpdate();
        this.updateById(user);
        return user;
    }

    /**
     * 修改密码
     *
     * @param oldPass 原密码
     * @param newPass 新密码
     * @return UserEnum
     */
    @Override
    public User changePassword(String oldPass, String newPass) {

        User currentUser = JwtUtil.getCurrentUserOfToken(); // 从token中获取用户信息（只包含用户id和用户名）
        if (currentUser == null)
            return null;
        User user = this.getById(currentUser.getUserId());

        String saltOldPass = addSaltForPassword(oldPass, user.getSalt());

        // 判断用户提供的原密码是否正确
        if (!saltOldPass.equals(user.getPassword()))
            return null;

        // 修改密码
        String saltNewPass = addSaltForPassword(newPass, user.getSalt());// shiro中加密必须要用Md5Hash
        user.setPassword(saltNewPass);
        user.setUpdate();
        this.updateById(user);
        return user;
    }

    /**
     * 根据id删除一条用户数据，需要权限，待续……
     *
     * @param id 用户id
     * @return Boolean
     */
    @Override
    public Boolean deleteById(Long id) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getDelFlag, DelFlagEnum.NORMAL.getCode())
                .and(e -> e.eq(User::getUserId, id));

        // 判断用户是否存在
        User user = this.getOne(queryWrapper);
        if (user == null)
            return false;

        // 做逻辑删除
        user.setDelFlag(DelFlagEnum.DELETE.getCode());
        user.setUpdate();
        this.updateById(user);

        return true;
    }


    @Override
    public IPage<User> getList(Map<String, Object> param) {

        Integer type = (Integer) param.get("type");
        String name = (String) param.get("username");
        String company = (String) param.get("Company");
        Integer currPage = (int) param.get("currPage");
        Integer size = (int) param.get("size");

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (param.containsKey("type") && (int) param.get("type") == -1)
            queryWrapper.lambda().eq(User::getDelFlag, DelFlagEnum.NORMAL.getCode())
                    .and(e -> e.eq(User::getType, 0).or().eq(User::getType, 1))
                    .like(name != null, User::getName, name)
                    .like(company != null, User::getCompanyName, company);
        else
            queryWrapper.lambda().eq(User::getDelFlag, DelFlagEnum.NORMAL.getCode())
                    .eq(type != null, User::getType, type)
                    .like(name != null, User::getName, name)
                    .like(company != null, User::getCompanyName, company);

        Page<User> page = new Page<>(currPage, size);
        return this.page(page, queryWrapper);
    }


    /*====================   私有方法   ==================== */

    /**
     * 密码加盐
     *
     * @param password 用户密码
     * @param salt     密码加盐
     * @return 加盐加密后的密码
     */
    private String addSaltForPassword(String password, String salt) {
        return new Md5Hash(password, salt, 2).toString();// shiro中默认加密必须要用Md5Hash
    }
}


