package com.backend.backend.base;

import com.backend.backend.common.Tools;
import com.backend.backend.enums.DelFlagEnum;
import com.backend.backend.jwt.JwtUtil;
import com.backend.backend.model.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class BaseEntity {
    // 删除标志 正常：0 删除：1
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ApiModelProperty(value = "删除标志", hidden = true)
    private Integer delFlag;

    // 创建者name
    @ApiModelProperty(value = "创建者", hidden = true)
    private String createBy;

    // 创建者Id
    @ApiModelProperty(value = "创建者Id", hidden = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long createById;


    // 创建时间
    @ApiModelProperty(value = "创建时间", hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;


    // 更新者name
    @ApiModelProperty(value = "更新者名称", hidden = true)
    private String updateBy;


    // 更新者Id
    @ApiModelProperty(value = "更新者Id", hidden = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long updateById;

    // 更新时间

    @ApiModelProperty(value = "更新时间", hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;


    // 备注
    @ApiModelProperty(value = "备注,这个字段有必要时传入。")
    private String remark;

    public void setUpdate() {
        // user
        User user = JwtUtil.getCurrentUserOfToken();
        this.updateById = user == null ? 0L : user.getUserId();
        this.updateBy = user == null ? "" : user.getName();
        this.updateTime = Tools.getGMT8Time();
        this.setCreateById(null);
        this.setCreateBy(null);
        this.setCreateTime(null);
    }

    public void setCreate() {
        // user
        User user = JwtUtil.getCurrentUserOfToken();
        this.createById = user == null ? 0L : user.getUserId();
        this.createBy = user == null ? "" : user.getName();
        this.updateById = user == null ? 0L : user.getUserId();
        this.updateBy = user == null ? "" : user.getName();
        this.updateTime = Tools.getGMT8Time();
        this.createTime = this.updateTime;
        this.delFlag = DelFlagEnum.NORMAL.getCode();
    }

    /**
     * 逻辑删除调用这个方法
     */
    public void delete() {
        this.delFlag = DelFlagEnum.DELETE.getCode();
    }
}
