package com.dmkj.plcdevice.config;

import com.dmkj.plcdevice.dto.DictDataDTO;
import com.dmkj.plcdevice.dto.DictDTO;
import com.dmkj.plcdevice.dto.UserDTO;
import com.dmkj.plcdevice.service.DictService;
import com.dmkj.plcdevice.service.UserService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    private final UserService userService;
    private final DictService dictService;

    public DataInitializer(UserService userService, DictService dictService) {
        this.userService = userService;
        this.dictService = dictService;
    }

    @Override
    public void run(ApplicationArguments args) {
        initAdmin();
        initDict();
    }

    private void initAdmin() {
        if (userService.count() > 0) {
            return;
        }
        UserDTO admin = new UserDTO();
        admin.setUsername("admin");
        admin.setPassword("123456");
        admin.setNickname("管理员");
        admin.setEmail("admin@dmkj.com");
        admin.setPhone("13800000000");
        admin.setRole("admin");
        admin.setStatus(1);
        userService.create(admin);

        UserDTO user = new UserDTO();
        user.setUsername("test");
        user.setPassword("123456");
        user.setNickname("测试用户");
        user.setRole("user");
        user.setStatus(1);
        userService.create(user);
    }

    private void initDict() {
        if (dictService.countDict() > 0) {
            return;
        }
        DictDTO status = new DictDTO();
        status.setDictType("sys_status");
        status.setDictName("系统状态");
        status.setRemark("通用启用/禁用状态");
        dictService.createDict(status);

        DictDataDTO s1 = new DictDataDTO();
        s1.setDictType("sys_status");
        s1.setLabel("启用");
        s1.setValue("1");
        s1.setSort(1);
        dictService.createDictData(s1);

        DictDataDTO s2 = new DictDataDTO();
        s2.setDictType("sys_status");
        s2.setLabel("禁用");
        s2.setValue("0");
        s2.setSort(2);
        dictService.createDictData(s2);

        DictDTO gender = new DictDTO();
        gender.setDictType("sys_gender");
        gender.setDictName("性别");
        dictService.createDict(gender);

        DictDataDTO g1 = new DictDataDTO();
        g1.setDictType("sys_gender");
        g1.setLabel("男");
        g1.setValue("male");
        g1.setSort(1);
        dictService.createDictData(g1);

        DictDataDTO g2 = new DictDataDTO();
        g2.setDictType("sys_gender");
        g2.setLabel("女");
        g2.setValue("female");
        g2.setSort(2);
        dictService.createDictData(g2);
    }
}
