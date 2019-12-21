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

/**
 * @Author: goodtimp
 * @Date: 2019/11/18 15:01
 * @description :  系统字典类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_dict")
@ApiModel("系统字典表")
public class SysDict extends BaseEntity {
    @ApiModelProperty("Id")
    @TableId
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long dictId;

    @ApiModelProperty("字典名称")
    private String dictName;

    @ApiModelProperty("字典标识")
    private String dictCode;

    @ApiModelProperty("字典tag")
    private String dictTag;

    @ApiModelProperty("排序顺序")
    private Integer orderNumber;

    @ApiModelProperty("字典链接")
    private String dictUrl;

    @ApiModelProperty("父级Id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long parentId;}
