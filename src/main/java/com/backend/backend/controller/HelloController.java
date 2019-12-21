package com.backend.backend.controller;

import com.backend.backend.common.model.ResponseModel;
import com.backend.backend.jwt.JwtUtil;
import com.backend.backend.model.entity.User;
import com.backend.backend.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Api("测试")
@RestController
public class HelloController {
    @Autowired
    private UserService userServiceImpl;

    @GetMapping("/")

    public String hello() {
        return "hello world!";
    }
    
    @ApiOperation(value = "注册", notes = "注册用户")
    @PostMapping("signIn")

    public ResponseModel signIn(String name, String password, String userPhone) {
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        user.setUserPhone(userPhone);
        User sqlUser = userServiceImpl.signIn(user);
        if (sqlUser == null) {
            return ResponseModel.fail("账号已存在");
        }
        return ResponseModel.success("注册成功");
    }
}
