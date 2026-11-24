package com.dm.device.model;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

/**
 * APP 端响应体（原 dispatch 项目 com.src.android.sipAndroid.model.R）
 */
@Getter
@Setter
public class R {
    private Boolean success;
    private String msg;
    private String data;

    private static String response(Boolean success, String msg, Object data) {
        R responseData = new R();
        responseData.setSuccess(success);
        responseData.setMsg(msg);
        responseData.setData(new Gson().toJson(data));
        return new Gson().toJson(responseData);
    }

    public static String success(String msg, Object data) {
        return R.response(true, msg, data);
    }

    public static String success(String msg) {
        return R.response(true, msg, null);
    }

    public static String fail(String msg) {
        return R.response(false, msg, null);
    }
}
