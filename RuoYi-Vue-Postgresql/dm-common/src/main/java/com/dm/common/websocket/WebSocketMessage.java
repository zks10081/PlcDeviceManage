package com.dm.common.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * WebSocket 消息载体（原 dispatch 项目 com.src.android.sipAndroid.model.WebSocketMessage）
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WebSocketMessage {
    private int code;

    private String name;

    private String text;

    public WebSocketMessage(int code, String text) {
        this.code = code;
        this.text = text;
    }
}
