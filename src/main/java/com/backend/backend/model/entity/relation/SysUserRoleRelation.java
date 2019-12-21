package com.backend.backend.model.entity.relation;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: goodtimp
 * @Date: 2019/11/8 9:02
 * @description :  用户角色对应表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_user_role_relation")
public class SysUserRoleRelation {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long roleId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;
}
