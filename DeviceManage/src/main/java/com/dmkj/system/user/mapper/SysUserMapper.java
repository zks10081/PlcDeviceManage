package com.dmkj.system.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysUserMapper {

    public int isLogin(@Param("username")String username,@Param("password") String password);

}
