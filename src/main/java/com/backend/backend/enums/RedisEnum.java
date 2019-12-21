package com.backend.backend.enums;

import lombok.Getter;

/**
 * @Author: goodtimp
 * @Date: 2019/10/24 12:58
 * @description :  redis枚举类
 */
@Getter
public enum RedisEnum {

    REFRESH_TOKEN_PREFIX("refresh token的前缀", "token:refresh:"),
    SHIRO_CACHE_PREFIX("shiro缓存的前缀", "shiro:cache:"),
    PERMISSION_HASH_MAP("权限的hashMap的key值", "sys_permission"),
    ROLE_HASH_MAP("角色的hashMap的key值", "sys_role"),
    ROLE_PERMISSION_RELATION_HASH_MAP("角色权限对应表的key值", "relation_role:permission:");
    private String name;
    private String code;

    RedisEnum(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
