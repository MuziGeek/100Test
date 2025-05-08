package com.muzi.part010.controller;

import com.muzi.part010.websocket.WebSocketServer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket控制器
 */
@RestController
@RequestMapping("/websocket")
@RequiredArgsConstructor
public class WebSocketController {

    /**
     * 发送广播消息
     */
    @PostMapping("/broadcast")
    public Map<String, Object> broadcast(@RequestParam String message) {
        WebSocketServer.sendAll(message);
        Map<String, Object> result = new HashMap<>(2);
        result.put("code", 200);
        result.put("msg", "广播消息发送成功");
        return result;
    }

    /**
     * 发送私聊消息
     */
    @PostMapping("/sendTo/{toUserId}")
    public Map<String, Object> sendTo(
            @PathVariable String toUserId,
            @RequestParam String message,
            @RequestParam String fromUserId) {
        WebSocketServer.sendInfo(fromUserId, toUserId, message);
        Map<String, Object> result = new HashMap<>(2);
        result.put("code", 200);
        result.put("msg", "私聊消息发送成功");
        return result;
    }
} 