package com.ruoyi.device.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 通用响应体（原 dispatch 项目 com.src.android.sipAndroid.model.Result）
 */
@Getter
@Setter
public class Result<T> {

    private Boolean success;
    private String msg;
    private T data;

    public static <T> Result<T> success(String message, T data) {
        Result<T> r = new Result<T>();
        r.success = true;
        r.msg = message;
        r.data = data;
        return r;
    }

    public static <T> Result<T> success(String message) {
        Result<T> r = new Result<T>();
        r.success = true;
        r.msg = message;
        return r;
    }

    public static <T> Result<T> fail(String message) {
        Result<T> r = new Result<T>();
        r.success = false;
        r.msg = message;
        return r;
    }
}
