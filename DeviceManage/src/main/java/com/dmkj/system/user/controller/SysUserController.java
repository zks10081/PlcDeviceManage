package com.dmkj.system.user.controller;

import com.dmkj.common.aspect.annotation.Auth;
import com.dmkj.common.result.Result;
import com.dmkj.system.user.entity.SysUser;
import com.dmkj.system.user.service.SysUserService;
import com.dmkj.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class SysUserController {

    @Autowired
    SysUserService userService;

    // 登录
    @Auth
    @PostMapping("/login")
    public Result<String> login(@RequestBody SysUser user, @RequestParam String partition) {
        String username = user.getUsername();
        String password = user.getPassword();
        if(StringUtils.isBlank(username) || StringUtils.isBlank(password)){
            return Result.error("账户或者密码为null");
        }

        int isLogin = userService.isLogin(username, password);
        if(isLogin != 0){
            return Result.error("账户或者密码错误！");
        }
        Map<String, Object> map = new HashMap<>();
        //令牌信息
        map.put("name", username);
        String token = JwtUtil.getToken(map);
        Map<String, Object> authInfo = new HashMap<String, Object>(2) {{
            put("token", token);
            put("user", username);
        }};
        RequestHolder.setSessionValue(RequestHolder.LOGIN_SESSION_KEY,account);
        return ResponseEntity.ok(authInfo);

        return null;
    }

}
