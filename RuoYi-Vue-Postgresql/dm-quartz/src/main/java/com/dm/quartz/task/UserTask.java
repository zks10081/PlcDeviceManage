package com.dm.quartz.task;

import com.dm.user.service.IDmUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component("UserTask")
public class UserTask {

    @Autowired
    private IDmUserService dmUserService;

    /***
     * 设备点位值进行计算转录
     */
    public void syncUserData(){
//        if(true)return;
        dmUserService.syncUserData();
    }
}
