package com.ruoyi.common.websocket;

import com.google.gson.Gson;
import com.ruoyi.common.websocket.vo.WebSocketCode;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket 通用处理器（连接管理 + 广播）。
 * 对 handleTextMessage 中需要 system 模块 SpeakGroupService 的业务逻辑，
 * 由 ruoyi-system 的 SpeakMessageHandler 子类实现。
 */
@Slf4j
@Component
public class MessageHandler extends TextWebSocketHandler {

    private static Gson myGson;

    // 存储所有连接的会话，使用线程安全的集合
    public static final Map<String, WebSocketSession> userSessions = Collections.synchronizedMap(new HashMap<>());
    public static final Map<String, String> userHosts = Collections.synchronizedMap(new HashMap<>());

    // 当新连接建立时：将连接添加到集合中
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        URI uri = session.getUri();
        if (uri == null) {
            session.close(CloseStatus.BAD_DATA);
        } else {
            String userNumber = parseNumberFromQuery(uri.getQuery());
            if (userNumber != null) {
                log.info("{}连接到服务端", userNumber);
                userSessions.put(userNumber, session);
            } else {
                session.close(CloseStatus.BAD_DATA);
            }
        }
    }

    // 当连接关闭时：从集合中移除连接
    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        log.info("{}断开连接", session.getAttributes().get("userNumber"));
        userSessions.values().remove(session);
    }

    // 通用发送信息（默认对讲消息码，兼容 SpeakGroupController）
    public void broadcast(ArrayList<String> numberList, Object message) {
        broadcast(numberList, message, WebSocketCode.SPEAK);
    }

    // 通用发送信息（可指定消息码，如设备报警 DEVICE_ALARM）
    public void broadcast(ArrayList<String> numberList, Object message,int code) {
        if (myGson == null) {
            myGson = new Gson();
        }
        WebSocketMessage webSocketMessage = new WebSocketMessage(code,myGson.toJson(message));
        broadcast(numberList,myGson.toJson(webSocketMessage));
    }


    // 广播消息：给指定用户列表
    public void broadcast(ArrayList<String> numberList, String content) {
        numberList.forEach(number -> {
            if (userSessions.containsKey(number) && userSessions.get(number).isOpen()) {
                try {
                    userSessions.get(number).sendMessage(new TextMessage(content));
                } catch (Exception ignored) {
                }
            }
        });
    }

    // 解析参数：解析 query 中的 number 参数
    private String parseNumberFromQuery(String query) {
        if (query == null) return null;
        for (String param : query.split("&")) {
            String[] kv = param.split("=");
            if (kv.length == 2 && kv[0].equals("number")) {
                try {
                    return kv[1];
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }
}
