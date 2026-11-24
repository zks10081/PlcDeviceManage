package com.dm.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.dm.common.opc.OpcProperties;
import com.dm.common.utils.HttpClientUtils;
import com.dm.common.utils.StringUtils;
import com.dm.device.domain.AlarmInfo;
import com.dm.user.domain.DmUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpUtils {

    private static Gson myGson = new Gson();
    /** 发送报警专用：服务端 AlarmInfo.alarmDate 要求 yyyy-MM-dd 格式 */
    private static Gson alarmGson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();


    public static void sendAlarm(AlarmInfo alarmInfo){
        // 动态读取配置，避免 static 字段在 Spring 绑定配置前已初始化为 null
        String ip = OpcProperties.dispatchIp;
        String port = OpcProperties.dispatchPort;
        String json = alarmGson.toJson(alarmInfo);
        String result = HttpClientUtils.doPost(StringUtils.format("http://{}:{}/device/sendAlarm", ip, port), json);
        System.out.println("发送报警通知响应：" + result);
    }

    public static IPage<DmUser> getUserAll(Map<String, Object> paramMap){
        // BUG 修复：原地址 /dmUser/sendAlarm 是发送告警接口，用户分页接口应为 /dmUser/queryAll
        // 动态读取配置，避免 static 字段在 Spring 绑定配置前已初始化为 null
        String ip = OpcProperties.dispatchIp;
        String port = OpcProperties.dispatchPort;
        String str = HttpClientUtils.doGet(StringUtils.format("http://{}:{}/dmUser/queryAll", ip, port), paramMap);

        Page<DmUser> page = myGson.fromJson(str, Page.class);
        return page;
    }

}
