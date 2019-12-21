package com.backend.backend.model.entity.sys;

import com.backend.backend.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @Author: goodtimp
 * @Date: 2019/11/5 16:31
 * @description :  权限角色表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_role")
@ApiModel(value = "权限角色类")
public class SysRole extends BaseEntity {
    @TableId
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "用户Id",name = "roleId")
    private Long roleId;

    @NotNull
    @ApiModelProperty(value = "角色名称",name = "roleName")
    private String roleName;

    @ApiModelProperty(value = "角色描述",name = "roleDescribe")
    private String roleDescribe;
}
