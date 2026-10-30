package com.src.model.plcdevice.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dmkj.plcdevice.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
