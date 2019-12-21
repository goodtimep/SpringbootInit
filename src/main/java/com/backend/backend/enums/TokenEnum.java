package com.backend.backend.enums;

import lombok.Getter;

/**
 * @Author: goodtimp
 * @Date: 2019/10/24 11:34
 * @description :
 */
@Getter
public enum TokenEnum {
    ACCESS_TOKEN_EXPIRE_TIME("token过期时间(s)", "10000"),
    PAYLOAD_USER_ID_TAG("token有效载荷中用户Id标识", "userId"),
    PAYLOAD_USER_NAME_TAG("token有效载荷中用户name标识", "userName"),
    PAYLOAD_USER_TYPE_TAG("token有效载荷中用户type标识", "userType"), //0:甲方； 1:乙方；2:普通管理员;3:系统管理员
    PAYLOAD_ROLE_TAG("token有效载荷中角色Id标识（逗号隔开）", "roleIds"),
    PAYLOAD_CREATE_TIME_TAG("token有效载荷中创建时间标识", "createTime"),
    REFRESH_TOKEN_EXPIRE_TIME("refreshToken有效载荷中创建时间标识(s)（7天）", "86400"),
    ENCRYPT_JWT_KEY("accessToken JWT认证加密私钥(Base64加密)后的值", "bXklMjBuYW1lJTIwaXMlMjBnb29kdGltcA=="),
    ENCRYPT_REFRESH_JWT_KEY("refreshToken认证加密私钥(Base64加密)后的值", "JXU3MjMxJXU0RjYwJXU1NTRBJXU1OTBGJXU2OEE2JXU2NUNC"),
    SHIRO_CACHE_EXPIRE_TIME("Shiro缓存过期时间-5V分钟-5*60(秒为单位)(一般设置与AccessToken过期时间一致)", "1800"),
    SHIRO_CACHE_DELETE_TOKEN_PREFIX("删除refreshToken后的token前缀", "token:"),
    SHIRO_CACHE_DELETE_TOKEN_EXPIRE_TIME("删除refreshToken后的token存在时间", "10000");
    private String name;
    private String code;

    TokenEnum(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
