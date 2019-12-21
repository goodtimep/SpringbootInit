package com.backend.backend.model.entity.relation;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: goodtimp
 * @Date: 2019/11/8 9:01
 * @description :  角色权限对应表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_role_permission_relation")
public class SysRolePermissionRelation {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long permissionId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long roleId;
}
