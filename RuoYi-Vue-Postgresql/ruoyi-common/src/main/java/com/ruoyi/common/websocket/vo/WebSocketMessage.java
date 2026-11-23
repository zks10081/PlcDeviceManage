package com.ruoyi.common.websocket.vo;

import lombok.*;

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
