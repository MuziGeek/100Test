package com.muzi.part010.websocket;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * WebSocket服务端
 */
@Slf4j
@Component
@ServerEndpoint("/websocket/{userId}")
public class WebSocketServer {

    // 记录当前在线连接数
    private static final AtomicInteger ONLINE_COUNT = new AtomicInteger(0);
    
    // 存放所有在线的客户端
    private static final Map<String, WebSocketServer> CLIENTS = new ConcurrentHashMap<>();
    
    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    
    // 客户端标识
    private String userId = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;
        
        // 如果已存在相同userId的WebSocket连接，先移除
        if (CLIENTS.containsKey(userId)) {
            CLIENTS.remove(userId);
            CLIENTS.put(userId, this);
        } else {
            CLIENTS.put(userId, this);
            // 在线数加1
            ONLINE_COUNT.incrementAndGet();
        }
        
        log.info("有新连接加入：{}，当前在线人数为：{}", userId, ONLINE_COUNT.get());
        
        try {
            JSONObject result = new JSONObject();
            result.set("type", "connect");
            result.set("userId", userId);
            result.set("onlineCount", ONLINE_COUNT.get());
            sendMessage(JSONUtil.toJsonStr(result));
        } catch (IOException e) {
            log.error("WebSocket IO异常");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (CLIENTS.containsKey(userId)) {
            CLIENTS.remove(userId);
            // 在线数减1
            ONLINE_COUNT.decrementAndGet();
            log.info("有一连接关闭：{}，当前在线人数为：{}", userId, ONLINE_COUNT.get());
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到来自客户端 {} 的信息:{}", userId, message);
        
        try {
            JSONObject jsonObject = JSONUtil.parseObj(message);
            String type = jsonObject.getStr("type");
            String targetId = jsonObject.getStr("targetId");
            String content = jsonObject.getStr("content");
            
            // 根据消息类型处理不同业务
            switch (type) {
                case "chat":
                    // 私聊消息
                    sendInfo(userId, targetId, content);
                    break;
                case "broadcast":
                    // 广播消息
                    sendAll(content);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            log.error("解析WebSocket消息异常: {}", e.getMessage());
        }
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket发生错误：{}，Session ID：{}", error.getMessage(), session.getId());
    }

    /**
     * 发送消息
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 群发自定义消息
     */
    public static void sendAll(String message) {
        log.info("推送消息到所有客户端，推送内容：{}", message);
        for (WebSocketServer item : CLIENTS.values()) {
            try {
                JSONObject result = new JSONObject();
                result.set("type", "broadcast");
                result.set("content", message);
                item.sendMessage(JSONUtil.toJsonStr(result));
            } catch (IOException e) {
                log.error("WebSocket群发消息异常: {}", e.getMessage());
            }
        }
    }

    /**
     * 指定发送消息
     */
    public static void sendInfo(String fromUserId, String toUserId, String content) {
        log.info("推送消息到用户：{}，推送内容：{}", toUserId, content);
        if (CLIENTS.containsKey(toUserId)) {
            try {
                JSONObject result = new JSONObject();
                result.set("type", "chat");
                result.set("fromUserId", fromUserId);
                result.set("content", content);
                CLIENTS.get(toUserId).sendMessage(JSONUtil.toJsonStr(result));
            } catch (IOException e) {
                log.error("WebSocket指定发送消息异常: {}", e.getMessage());
            }
        } else {
            log.warn("用户 {} 不在线", toUserId);
        }
    }
} 