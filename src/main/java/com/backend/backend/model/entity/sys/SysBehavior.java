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
 * @author ipso
 * @description  系统用户行为表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_behavior")
@ApiModel("系统用户行为表")
public class SysBehavior extends BaseEntity {

   @ApiModelProperty("Id")
   @TableId
   @JsonFormat(shape = JsonFormat.Shape.STRING)
   private Long behaviorId;

   @ApiModelProperty("用户id")
   private Long userId;

   @ApiModelProperty("操作表的id")
   private Long operationId;

   @ApiModelProperty("行为类型：点赞1, 评论2")
   private Integer type;

   @ApiModelProperty("字典表id")
   private Long dictId;

   @ApiModelProperty("字典表名称，方便读取信息")
   private String dictName;

   @ApiModelProperty("字典code，放主要数据，方便读取数据")
   private String dictCode;

}
