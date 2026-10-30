package com.dmkj.system.user.service.Impl;

import com.dmkj.system.user.mapper.SysUserMapper;
import com.dmkj.system.user.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    SysUserMapper userMapper;

    @Override
    public int isLogin(String username, String password) {
        return userMapper.isLogin(username,password);
    }
}
