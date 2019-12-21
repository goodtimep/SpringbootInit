package com.backend.backend.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.backend.backend.common.utils.Base64ConvertUtil;
import com.backend.backend.enums.RedisEnum;
import com.backend.backend.enums.TokenEnum;
import com.backend.backend.exception.TokenException;
import com.backend.backend.model.entity.User;
import com.backend.backend.model.entity.relation.SysRolePermissionRelation;
import com.backend.backend.model.entity.sys.SysPermission;
import com.backend.backend.redis.RedisUtil;
import com.backend.backend.service.SysPermissionService;
import com.backend.backend.service.SysRolePermissionRelationService;
import com.backend.backend.service.SysUserRoleRelationService;
import com.backend.backend.service.UserService;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.backend.backend.enums.TokenEnum.*;

/**
 * @Author: goodtimp
 * @Date: 2019/10/1 22:44
 * @description :  jwt工具类
 */
@Component
public class JwtUtil {
    private static UserService userServiceImpl;
    private static SysUserRoleRelationService sysUserRoleRelationServiceImpl;
    private static SysPermissionService sysPermissionServiceImpl;
    private static SysRolePermissionRelationService sysRolePermissionRelationServiceImpl;

    /**
     * 需要filter注入那时还不能注入，所以改成静态
     *
     * @param userServiceImpl
     */
    @Autowired
    public void setUserService(UserService userServiceImpl) {
        JwtUtil.userServiceImpl = userServiceImpl;
    }

    /**
     * 需要filter注入那时还不能注入，所以改成静态
     *
     * @param sysRolePermissionRelationServiceImpl
     */
    @Autowired
    public void setSysRolePermissionRelationService(SysRolePermissionRelationService sysRolePermissionRelationServiceImpl) {
        JwtUtil.sysRolePermissionRelationServiceImpl = sysRolePermissionRelationServiceImpl;
    }

    /**
     * 需要filter注入那时还不能注入，所以改成静态
     *
     * @param sysUserRoleRelationServiceImpl
     */
    @Autowired
    public void setSysUserRoleRelationService(SysUserRoleRelationService sysUserRoleRelationServiceImpl) {
        JwtUtil.sysUserRoleRelationServiceImpl = sysUserRoleRelationServiceImpl;
    }

    @Autowired
    public void setSysPermissionService(SysPermissionService sysPermissionServiceImpl) {
        JwtUtil.sysPermissionServiceImpl = sysPermissionServiceImpl;
    }

    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    /**
     * 判断是否存在refreshToken决定签发token
     *
     * @param userId
     * @param name
     * @param type   类型
     * @param token  原token，用于刷新token。如果为null则签发新的token
     * @return
     */
    public static String signAndIssueToken(String userId, String name, String type, String token) {
        String refreshToken;
        String currTimeStamp = String.valueOf(System.currentTimeMillis()); // 获取当前时间戳
        // 刷新token
        if (token != null) {
            String time = getClaim(token, PAYLOAD_CREATE_TIME_TAG.getCode());
            refreshToken = getRefreshToken(userId, time);
            // 刷新token，并返回
            if (refreshToken != null) {
                if (refreshToken.indexOf(SHIRO_CACHE_DELETE_TOKEN_PREFIX.getCode()) >= 0) {
                    return refreshToken.replace(SHIRO_CACHE_DELETE_TOKEN_PREFIX.getCode(), "");
                }
                String newToken = JwtUtil.sign(userId, name, type, currTimeStamp);
                // 刷新refreshToken
                updateRefreshToken(token, currTimeStamp, newToken);
                // 签发新的token
                return newToken;
            }
            return null;
        }
        // 签发新的token

        // 添加refreshToken
        JwtUtil.addRefreshToken(userId, name, currTimeStamp);
        return JwtUtil.sign(userId, name, type, currTimeStamp); // 生成新的token
    }

    /**
     * 传入userId和time得到对应的redis 的key
     *
     * @param userId
     * @param time
     * @return
     */
    public static String getRedisKey(String userId, String time) {
        return RedisEnum.REFRESH_TOKEN_PREFIX.getCode() + userId + ":" + time;
    }

    /**
     * 从redis中得到refreshToken，不存在返回null
     *
     * @param userId 用户Id
     * @param time   签发时间
     * @return
     */
    public static String getRefreshToken(String userId, String time) {
        return (String) RedisUtil.get(getRedisKey(userId, time));
    }

    /**
     * 验证token是否正确
     *
     * @param token
     * @return
     */
    public static boolean verify(String token) {
        try {
            // 帐号加JWT私钥解密
            String secret = getClaim(token, PAYLOAD_USER_ID_TAG.getCode()) + Base64ConvertUtil.decode(ENCRYPT_JWT_KEY.getCode());
            Algorithm algorithm = Algorithm.HMAC256(secret);  // 解密 验证正确性
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (UnsupportedEncodingException e) {
            logger.error("JWTToken认证解密出现UnsupportedEncodingException异常:" + e.getMessage());
            throw new TokenException("JWTToken认证解密出现UnsupportedEncodingException异常:" + e.getMessage());
        }
    }


    /**
     * 获得Token中有效载荷部分信息
     *
     * @param token
     * @param claim
     * @return
     */
    public static String getClaim(String token, String claim) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            // 只能输出String类型，如果是其他类型返回null
            return jwt.getClaim(claim).asString();
        } catch (JWTDecodeException e) {
            logger.error("解密Token中的公共信息出现JWTDecodeException异常:" + e.getMessage());
            throw new JWTDecodeException("解密Token中的公共信息出现JWTDecodeException异常:" + e.getMessage());
        }
    }

    /**
     * 生成签名 todo:需要加角色信息
     *
     * @param userId            帐号id
     * @param name              用户名称
     * @param currentTimeMillis
     * @return 返回加密的Token
     */
    public static String sign(String userId, String name, String type, String currentTimeMillis) {
        try {
            // 帐号加JWT私钥加密
            String secret = userId + Base64ConvertUtil.decode(ENCRYPT_JWT_KEY.getCode());
            // 此处过期时间是以毫秒为单位，所以乘以1000
            Date date = new Date(System.currentTimeMillis() + Long.parseLong(ACCESS_TOKEN_EXPIRE_TIME.getCode()) * 1000L);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            // 获取用户对应的角色Id
            List<Long> ids = sysUserRoleRelationServiceImpl.getRoleIdsByUserId(Long.parseLong(userId));
            StringBuffer roleIds = new StringBuffer();
            ids.stream().forEach(e -> roleIds.append(e + ","));
            // 附带account帐号信息
            return JWT.create()
                    .withClaim(PAYLOAD_USER_ID_TAG.getCode(), userId)
                    .withClaim(PAYLOAD_CREATE_TIME_TAG.getCode(), currentTimeMillis)
                    .withClaim(PAYLOAD_USER_NAME_TAG.getCode(), name)
                    .withClaim(PAYLOAD_USER_TYPE_TAG.getCode(), type)
                    .withClaim(PAYLOAD_ROLE_TAG.getCode(), roleIds.toString())
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            logger.error("JWTToken加密出现UnsupportedEncodingException异常:" + e.getMessage());
            throw new JWTDecodeException("JWTToken加密出现UnsupportedEncodingException异常:" + e.getMessage());
        }
    }

    /**
     * 创建一个refreshToken
     *
     * @param userId
     * @param name              用户名称
     * @param currentTimeMillis 创建时间的时间戳
     * @param date              过期时间
     * @return
     */
    public static String signRefreshToken(String userId, String name, String currentTimeMillis, Date date) {
        try {
            // 帐号加JWT私钥加密
            String secret = userId + Base64ConvertUtil.decode(ENCRYPT_REFRESH_JWT_KEY.getCode());
            Algorithm algorithm = Algorithm.HMAC256(secret);
            // 附带account帐号信息
            return JWT.create()
                    .withClaim(PAYLOAD_USER_ID_TAG.getCode(), userId)
                    .withClaim(PAYLOAD_CREATE_TIME_TAG.getCode(), currentTimeMillis)
                    .withClaim(PAYLOAD_USER_NAME_TAG.getCode(), name)
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            logger.error("JWTToken加密出现UnsupportedEncodingException异常:" + e.getMessage());
            throw new JWTDecodeException("JWTToken加密出现UnsupportedEncodingException异常:" + e.getMessage());
        }
    }

    /**
     * 创建一个refreshToken
     *
     * @param userId
     * @param name              用户名
     * @param currentTimeMillis 创建时间的时间戳
     * @return
     */
    public static String signRefreshToken(String userId, String name, String currentTimeMillis) {
        return signRefreshToken(userId, name, currentTimeMillis,
                // 此处过期时间是以毫秒为单位，所以乘以1000
                new Date(System.currentTimeMillis() + Long.parseLong(REFRESH_TOKEN_EXPIRE_TIME.getCode()) * 1000L));
    }

    /**
     * 往Redis中新增一个refreshToken
     *
     * @param userId
     * @param name              用户名
     * @param currentTimeMillis 签发时间
     * @return
     */
    public static String addRefreshToken(String userId, String name, String currentTimeMillis) {
        String refreshToken = signRefreshToken(userId, name, currentTimeMillis);
        RedisUtil.set(getRedisKey(userId, currentTimeMillis),
                refreshToken, Long.parseLong(REFRESH_TOKEN_EXPIRE_TIME.getCode()) * 1000L);
        return refreshToken;
    }

    /**
     * 删除某个token
     *
     * @param userId
     * @param time
     */
    public static void deleteRefreshToken(String userId, String time, String token) {
        RedisUtil.set(getRedisKey(userId, time), SHIRO_CACHE_DELETE_TOKEN_PREFIX.getCode() + token,
                Long.parseLong(SHIRO_CACHE_DELETE_TOKEN_EXPIRE_TIME.getCode()));
    }


    /**
     * 更新refreshToken ，如果不存在则创建，不刷新过期时间
     *
     * @param token
     * @param currentTimeMillis
     * @param newToken
     * @return
     */
    public static String updateRefreshToken(String token, String currentTimeMillis, String newToken) {
        try {
            String userId = getClaim(token, PAYLOAD_USER_ID_TAG.getCode());
            String oldTime = getClaim(token, PAYLOAD_CREATE_TIME_TAG.getCode());
            // 获取redis里面的原有token
            String refreshToken = getRefreshToken(userId, oldTime);
            // 获取过期时间
            Long expire = RedisUtil.getExpire(getRedisKey(userId, oldTime));
            // 删除原来token
            deleteRefreshToken(userId, oldTime, newToken);
            // 将原来的refreshToken换key存储新到redis
            RedisUtil.set(getRedisKey(userId, currentTimeMillis), refreshToken, expire);

            return refreshToken;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据token判断refreshToken是否存在和 正确
     *
     * @param token
     * @return
     */
    public static boolean judgeRefreshToken(String token) {
        try {
            // 获取userId
            String userId = JwtUtil.getClaim(token, PAYLOAD_USER_ID_TAG.getCode());
            String time = JwtUtil.getClaim(token, PAYLOAD_CREATE_TIME_TAG.getCode());
            // 获取refreshToken如果为null则不正确
            String refreshToken = getRefreshToken(userId, time);
            return refreshToken != null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }


    /**
     * 获取当前用户
     *
     * @return
     */
    public static User getCurrentUser() {
        if (SecurityUtils.getSubject().getPrincipals() == null) return null;
        String token = SecurityUtils.getSubject().getPrincipals().toString();
        return token == null ? null : userServiceImpl.getById(Long.parseLong(getClaim(token, PAYLOAD_USER_ID_TAG.getCode())));
    }

    /**
     * 获取当前用户token中的用户信息（userId、username）
     *
     * @return
     */
    public static User getCurrentUserOfToken() {
        if (SecurityUtils.getSubject().getPrincipals() == null) return null;
        String token = SecurityUtils.getSubject().getPrincipals().toString();
        String id = getClaim(token, PAYLOAD_USER_ID_TAG.getCode());
        String name = getClaim(token, PAYLOAD_USER_NAME_TAG.getCode());
        Integer type = Integer.parseInt(getClaim(token, PAYLOAD_USER_TYPE_TAG.getCode()));
        User user = new User();
        user.setUserId(Long.parseLong(id));
        user.setName(name);
        user.setName(name);
        user.setType(type);
        return user;
    }

    /**
     * 判断是否含有某些权限
     *
     * @param permissions
     * @return
     */
    public static boolean[] hasPermission(String... permissions) {
        return SecurityUtils.getSubject().isPermitted(permissions);
    }

    /**
     * 通过token得到权限列表 （redis中获取）
     *
     * @param token 传入null则获取当前
     * @return
     */
    public static List<SysPermission> getPermissionByToken(String token) {
        if (token == null) {
            token = SecurityUtils.getSubject().getPrincipals().toString();
        }
        // 当前无用户登录返回空
        if (token == null) return null;

        // 系统管理员默认拥有所有权限
        if (getClaim(token, PAYLOAD_USER_TYPE_TAG.getCode()).equals("3")) {
            return sysPermissionServiceImpl.getAllPermissionRedis();
        }

        // 获取roleId
        String roleIds = getClaim(token, PAYLOAD_ROLE_TAG.getCode());
        String[] temp = roleIds.split(",");
        List<Long> listRoleIds = new ArrayList<>();
        for (String item : temp) {
            if (item != null && item != "") listRoleIds.add(Long.parseLong(item));
        }
        // 获取权限id
        List<SysRolePermissionRelation> list = sysRolePermissionRelationServiceImpl.getPermissionIdsByRoleIdsForRedis(listRoleIds);
        // 获取权限
        List<SysPermission> result = sysPermissionServiceImpl
                .getPermissionByFromRedis(list.stream().map(e -> e.getPermissionId()).collect(Collectors.toList()));
        return result;
    }

    /**
     * 通过用户Id强制下线用户，如果用户未登录则不做操作
     *
     * @param userId
     * @return
     */
    public static void forcedOffLine(Long userId) {
        RedisUtil.clear(getRedisKey(userId.toString(), ""));
    }
}