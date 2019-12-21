package com.backend.backend.model.entity.sys;

import com.backend.backend.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @Author: goodtimp
 * @Date: 2019/11/5 16:39
 * @description :  权限表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_permission")
@ApiModel("权限表")
public class SysPermission extends BaseEntity {
    @ApiModelProperty("权限Id")
    @TableId
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long perId;

    @ApiModelProperty("权限name")
    @NotNull
    private String perName;

    @ApiModelProperty("权限父级Id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long parentId;

    @ApiModelProperty("权限接口")
    private String perUrl;

    /**
     * 权限标识 例如：user:save
     */
    @ApiModelProperty("权限标识")
    private String perCode;

    @ApiModelProperty("权限类型：M 菜单(存在子菜单) ；A 接口；L 菜单（不存在子集）；B 按钮")
    private char perType;
}
