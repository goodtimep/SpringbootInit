package com.backend.backend.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.backend.backend.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;

/**
 * @Author: goodtimp
 * @Date: 2019/9/19 17:24
 * @description :  用户表
 */
@Data
@TableName("sys_user")
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "用户实体类")
public class User extends BaseEntity {

    @TableId
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(name = "userId", value = "用户Id")
    private Long userId;

    /*@ApiModelProperty("0:甲方； 1:乙方；2:普通管理员;3:系统管理员")
    private Integer type;*/

    @NotNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ApiModelProperty(name = "password", value = "用户password")
    private String password;

    @NotNull
    @ApiModelProperty(name = "name", value = "userName")
    private String name;

    @NotNull
    @ApiModelProperty(name = "userPhone", value = "userPhone")
    private String userPhone;

    @NotNull
    @ApiModelProperty(name = "userEmail")
    private String userEmail;

    @ApiModelProperty(name = "avatar")
    private String avatar;

    // 0:甲方； 1:乙方；2:普通管理员;3:系统管理员，见enums.UserEnum
    @NotNull
    @ApiModelProperty(name = "type", value = "type")
    private Integer type;

    // 用户职位
    @ApiModelProperty(name = "userPost", value = "userPost")
    private String userPost;

    @ApiModelProperty(name = "companyName", value = "companyName")
    private String companyName;

    @ApiModelProperty(name = "companyPhone", value = "companyPhone")
    private String companyPhone;

    @ApiModelProperty(name = "companyEmail", value = "companyEmail")
    private String companyEmail;

    @ApiModelProperty(name = "companyUrl", value = "companyUrl")
    private String companyUrl;

    // 公司类型 （乙）公司类型：厂商、集成商、服务商、软件开发商、弱电公司、培训公司、其他
    @ApiModelProperty(name = "companyType", value = "companyType")
    private String companyType;

    @ApiModelProperty(name = "charterImage", value = "charterImage")
    private String charterImage;

    // 密码加盐
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String salt;

}
