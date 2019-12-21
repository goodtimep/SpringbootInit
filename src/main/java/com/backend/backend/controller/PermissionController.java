package com.backend.backend.controller;

import com.backend.backend.common.model.ResponseModel;
import com.backend.backend.jwt.JwtUtil;
import com.backend.backend.model.entity.User;
import com.backend.backend.model.entity.relation.SysRolePermissionRelation;
import com.backend.backend.model.entity.relation.SysUserRoleRelation;
import com.backend.backend.model.entity.sys.SysPermission;
import com.backend.backend.model.entity.sys.SysRole;
import com.backend.backend.service.SysPermissionService;
import com.backend.backend.service.SysRolePermissionRelationService;
import com.backend.backend.service.SysRoleService;
import com.backend.backend.service.SysUserRoleRelationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: goodtimp
 * @Date: 2019/11/10 20:34
 * @description :  权限控制器
 */
@Api(description = "测试时某些带参数的GET方法无法正常返回数据，请切换至POSTMan即可解决。")
@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor(onConstructor = @_(@Autowired))
public class PermissionController {

    final private SysRoleService sysRoleServiceImpl;
    final private SysRolePermissionRelationService sysRolePermissionRelationServiceImpl;
    final private SysPermissionService sysPermissionServiceImpl;
    final private SysUserRoleRelationService sysUserRoleRelationServiceImpl;

    @ApiOperation(hidden = true, value = "初始化权限redis")
    @GetMapping("/")
    public void initPermissionForRedis() {
        sysRoleServiceImpl.initRoleSqlForRedis();
        sysPermissionServiceImpl.initPermissionForRedis();
        sysRolePermissionRelationServiceImpl.initPermissionRoleRelation();
    }

    /**
     * 得到当前用户的角色信息
     *
     * @return
     */
    @ApiOperation(value = "得到当前用户的角色信息")

    @GetMapping("/role/getCurrent")
    @RequiresPermissions("userHasLogged")
    public ResponseModel getCurrentRole() {
        User user = JwtUtil.getCurrentUserOfToken();
        List<Long> roleIds = sysUserRoleRelationServiceImpl.getRoleIdsByUserId(user.getUserId());
        List<SysRole> list = sysRoleServiceImpl.getRoleIdsByIdsForRedis(roleIds);
        return ResponseModel.success("data", list);
    }

    /**
     * 得到当前用户的角色信息
     *
     * @return
     */
    @ApiOperation(value = "得到当前用户的权限信息")

    @GetMapping("/permission/getCurrent")
    @RequiresPermissions("userHasLogged")
    public ResponseModel getCurrentPermission() {
        List<SysPermission> list = sysPermissionServiceImpl.getCurrentPermission();
        return ResponseModel.success("data", list);
    }


    /**
     * 得到用户对应的角色Id
     *
     * @param userId
     * @return
     */
    @ApiOperation(value = "得到用户对应的角色Id", notes = "返回数组形式的Id")
    @ApiImplicitParam(name = "userId", dataType = "Long", value = "用户Id")
    @GetMapping("/relation/userRole/get")
    @RequiresPermissions("userRoleRelationGet")
    public ResponseModel getUserRoleRelation(@RequestParam("userId") Long userId) {
        List<Long> re = sysUserRoleRelationServiceImpl.getRoleIdsByUserId(userId);
        return ResponseModel.success("data", re.stream().map(e -> e.toString()).collect(Collectors.toList()));
    }


    /**
     * 增加用户对应的角色信息
     *
     * @param map
     * @return
     */
    @ApiOperation(value = "增加用户对应的角色信息", notes = "增加用户对应的角色信息，传入示例,如果roleIds为空则无效(不能为null)：{userId:123,roleIds:[121,12]}")
    @PostMapping("/relation/userRole/save")
    @RequiresPermissions("userRoleRelation:save")
    public ResponseModel addUserRoleRelation(@RequestBody Map<String, Object> map) {
        Long userId = Long.parseLong((String) map.get("userId"));
        List<String> roleIds = (List<String>) map.get("roleIds");
        List<SysUserRoleRelation> list = roleIds.stream().map(e -> new SysUserRoleRelation(Long.parseLong(e), userId)).collect(Collectors.toList());
        sysUserRoleRelationServiceImpl.addRoleForUserId(list);
        return ResponseModel.success("添加成功");
    }

    /**
     * 修改用户对应的角色信息
     *
     * @param map
     * @return
     */
    @ApiOperation(value = "修改用户对应的角色信息", notes = "修改用户对应的角色信息，修改为传入数据（多删少补），如果roleIds为空则清空(不能为null)：{userId:123,roleIds:[121,12]}")
    @PostMapping("/relation/userRole/update")
    @RequiresPermissions("userRoleRelationUpdate")
    public ResponseModel updateUserRoleRelation(@RequestBody Map<String, Object> map) {
        List<String> roleIds = (List<String>) map.get("roleIds");
        Long userId = Long.parseLong((String) map.get("userId"));
        List<SysUserRoleRelation> list = roleIds.stream().map(e -> new SysUserRoleRelation(Long.parseLong(e), userId)).collect(Collectors.toList());
        if (roleIds.isEmpty()) {
            sysUserRoleRelationServiceImpl.deleteRoleByUserId(userId, null);
            return ResponseModel.success("修改成功");
        }
        sysUserRoleRelationServiceImpl.updateRoleForUserId(list);
        return ResponseModel.success("修改成功");
    }

    /**
     * 得到所有角色信息
     *
     * @return
     */
    @ApiOperation(value = "获取所有角色信息")

    @GetMapping("/role/all")
    @RequiresPermissions("roleAll")
    public ResponseModel getAllRole() {
        List<SysRole> sysRoles = sysRoleServiceImpl.getRoleIdsByIds(null);
        return ResponseModel.success("data", sysRoles);
    }

    /**
     * 得到角色对应的权限Id
     *
     * @param roleId
     * @return
     */
    @ApiOperation(value = "得到角色对应的权限Id", notes = "返回数组形式的Id")
    @ApiImplicitParam(name = "roleId", dataType = "Long", value = "角色Id")
    @GetMapping("/relation/rolePermission/get")
    @RequiresPermissions("rolePermissionRelationGet")
    public ResponseModel getRolePermissionRelation(@RequestParam("roleId") Long roleId) {
        List<SysRolePermissionRelation> re = sysRolePermissionRelationServiceImpl.getIdsByRoleId(roleId);
        return ResponseModel.success("data", re.stream().map(e -> e.getPermissionId().toString()).collect(Collectors.toList()));
    }

    /**
     * 获取角色
     *
     * @param roleId
     * @return
     */
    @ApiOperation(value = "根据Id得到角色信息", notes = "返回角色信息")
    @ApiImplicitParam(name = "roleId", required = true, dataType = "Long", value = "角色Id")
    @GetMapping("/role/getById")
    @RequiresPermissions("roleById")
    public ResponseModel getRoleById(@RequestParam("roleId") Long roleId) {
        return ResponseModel.success("data", sysRoleServiceImpl.getById(roleId));
    }

    /**
     * 增加角色
     *
     * @param role
     * @return
     */
    @ApiOperation(value = "增加角色信息", notes = "返回添加的数据Id")
    @PostMapping("/role/save")
    @RequiresPermissions("roleSave")
    public ResponseModel saveRole(@RequestBody SysRole role) {
        sysRoleServiceImpl.addRole(role);
        return ResponseModel.success("添加成功").addExtend("id", role.getRoleId().toString());
    }

    /**
     * 更新角色
     *
     * @param role
     * @return
     */
    @ApiOperation(value = "更新角色信息", notes = "根据Id更新相应角色信息")
    @PostMapping("/role/update")
    @RequiresPermissions("roleUpdate")
    public ResponseModel updateRole(@RequestBody SysRole role) {
        sysRoleServiceImpl.updateRole(role);
        return ResponseModel.success("修改成功");
    }

    /**
     * 删除角色
     *
     * @param roleId
     * @return
     */
    @ApiOperation(value = "根据Id删除信息")
    @ApiImplicitParam(name = "roleId", required = true, dataType = "Long", value = "角色Id")
    @GetMapping("/role/deleteById")
    @RequiresPermissions("roleDelete")
    public ResponseModel deleteRoleById(@RequestParam("roleId") Long roleId) {
        sysRoleServiceImpl.deleteById(roleId); // 删除角色对应的权限关联表
        return ResponseModel.success("删除成功");
    }

    /**
     * 增加角色对应的权限信息
     *
     * @param map
     * @return
     */
    @ApiOperation(value = "增加角色对应的权限信息", notes = "增加角色对应的权限信息，传入示例 permissionIds不能为null：{roleId:1,permissionIds:[2,3]}")
    @PostMapping("/relation/rolePermission/save")
    @RequiresPermissions("rolePermissionRelation:save")
    public ResponseModel addRolePermissionRelation(@RequestBody Map<String, Object> map) {
        List<String> perIds = (List<String>) map.get("permissionIds");
        Long roleId = Long.parseLong((String) map.get("roleId"));
        List<SysRolePermissionRelation> list = perIds.stream().map(e -> new SysRolePermissionRelation(Long.parseLong(e), roleId)).collect(Collectors.toList());
        sysRolePermissionRelationServiceImpl.addPermissionWithRole(list);
        return ResponseModel.success("添加成功");
    }


    /**
     * 修改角色对应的权限信息
     *
     * @param map
     * @return
     */
    @ApiOperation(value = "修改角色对应的权限信息", notes = "修改角色对应的权限信息，修改为传入数据（多删少补），传入示例 permissionIds为[]清空所有权限，不能为null：{roleId:1,permissionIds:[2,3]}")
    @PostMapping("/relation/rolePermission/update")
    @RequiresPermissions("rolePermissionRelationUpdate")
    public ResponseModel updateRolePermissionRelation(@RequestBody Map<String, Object> map) {
        List<String> perIds = (List<String>) map.get("permissionIds");
        Long roleId = Long.parseLong((String) map.get("roleId"));
        List<SysRolePermissionRelation> list = perIds.stream().map(e -> new SysRolePermissionRelation(Long.parseLong(e), roleId)).collect(Collectors.toList());
        if (perIds.isEmpty()) {
            sysRolePermissionRelationServiceImpl.deletePermissionWithRole(roleId, null);
            return ResponseModel.success("修改成功");
        }
        sysRolePermissionRelationServiceImpl.updatePermissionForRole(list);
        return ResponseModel.success("修改成功");
    }


    /**
     * 增加角色
     *
     * @param perId
     * @return
     */
    @ApiOperation(value = "根据Id得到权限信息", notes = "返回权限信息")
    @ApiImplicitParam(name = "perId", dataType = "Long", value = "权限Id")
    @GetMapping("/permission/getById")
    @RequiresPermissions("permissionGetById")
    public ResponseModel getPermissionById(@RequestParam("perId") Long perId) {
        return ResponseModel.success("data", sysPermissionServiceImpl.getById(perId));
    }

    /**
     * 增加权限
     *
     * @param permission
     * @return
     */
    @ApiOperation(value = "增加权限信息", notes = "返回添加的数据Id")
    @PostMapping("/permission/save")
    @RequiresPermissions("permissionSave")
    public ResponseModel savePermission(@RequestBody SysPermission permission) {
        sysPermissionServiceImpl.addPermissionWithRole(permission);
        return ResponseModel.success("添加成功").addExtend("id", permission.getPerId().toString());
    }

    /**
     * 更新权限
     *
     * @param permission
     * @return
     */
    @ApiOperation(value = "更新权限信息", notes = "返回添加的数据Id")
    @PostMapping("/permission/update")
    @RequiresPermissions("permissionUpdate")
    public ResponseModel updatePermission(@RequestBody SysPermission permission) {
        sysPermissionServiceImpl.updatePermissionForRole(permission);
        return ResponseModel.success("修改成功");
    }

    /**
     * 得到所有权限
     *
     * @return
     */
    @ApiOperation(value = "获取所有的权限信息")

    @GetMapping("/permission/all")
    @RequiresPermissions("permissionAll")
    public ResponseModel getAllPermission() {
        List<SysPermission> sysPermissionList = sysPermissionServiceImpl.getAllPermission();
        return ResponseModel.success("data", sysPermissionList);
    }
}
