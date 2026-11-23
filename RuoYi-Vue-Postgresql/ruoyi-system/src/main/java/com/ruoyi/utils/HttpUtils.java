package com.ruoyi.utils;

import com.google.gson.Gson;
import com.ruoyi.common.utils.HttpClientUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.device.domain.AlarmInfo;

public class HttpUtils {

    private static String IP = "192.168.1.106";
    private static String Port = "80";


    public static void sendAlarm(AlarmInfo alarmInfo){

        Gson myGson = new Gson();

        HttpClientUtils.doPost(StringUtils.format("http://{}:{}/device/sendAlarm",IP,Port),myGson.toJson(alarmInfo));
    }

}
