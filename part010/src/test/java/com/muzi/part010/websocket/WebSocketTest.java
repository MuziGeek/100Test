package com.muzi.part010.websocket;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * WebSocket测试类
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class WebSocketTest {

    @LocalServerPort
    private int port;

    private TestWebSocketClient client1;
    private TestWebSocketClient client2;
    private final CountDownLatch latch = new CountDownLatch(1);

    @BeforeEach
    public void setup() throws URISyntaxException {
        String userId1 = "testUser1";
        String userId2 = "testUser2";
        String baseUrl = "ws://localhost:" + port + "/websocket/";

        client1 = new TestWebSocketClient(new URI(baseUrl + userId1), "客户端1");
        client2 = new TestWebSocketClient(new URI(baseUrl + userId2), "客户端2");

        // 连接服务器
        client1.connect();
        client2.connect();

        // 等待连接建立
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            log.error("等待连接时发生中断", e);
            Thread.currentThread().interrupt();
        }
    }

    @AfterEach
    public void tearDown() {
        // 关闭连接
        if (client1 != null && client1.isOpen()) {
            client1.close();
        }
        if (client2 != null && client2.isOpen()) {
            client2.close();
        }
    }

    /**
     * 测试WebSocket服务器连接
     */
    @Test
    public void testWebSocketConnection() throws InterruptedException {
        log.info("测试WebSocket连接...");
        
        // 验证客户端是否成功连接
        assert client1.isOpen() : "客户端1连接失败";
        assert client2.isOpen() : "客户端2连接失败";
        
        log.info("WebSocket连接测试通过");
    }

    /**
     * 测试发送广播消息
     */
    @Test
    public void testBroadcastMessage() throws InterruptedException {
        log.info("测试广播消息...");
        
        // 发送广播消息
        String message = "这是一条广播测试消息_" + RandomUtil.randomString(6);
        client1.sendBroadcastMessage(message);
        
        // 等待消息传输
        boolean received = latch.await(5, TimeUnit.SECONDS);
        
        // 打印接收到的消息
        log.info("客户端1收到的消息: {}", client1.getReceivedMessages());
        log.info("客户端2收到的消息: {}", client2.getReceivedMessages());
        
        // 验证客户端2是否收到广播消息
        assert client2.getReceivedMessages().stream()
                .anyMatch(msg -> msg.contains(message)) : "广播消息接收失败";
        
        log.info("广播消息测试通过");
    }

    /**
     * 测试发送私聊消息
     */
    @Test
    public void testPrivateMessage() throws InterruptedException {
        log.info("测试私聊消息...");
        
        // 客户端1给客户端2发送私聊消息
        String message = "这是一条私聊测试消息_" + RandomUtil.randomString(6);
        client1.sendPrivateMessage("testUser2", message);
        
        // 等待消息传输
        Thread.sleep(1000);
        
        // 打印接收到的消息
        log.info("客户端2收到的消息: {}", client2.getReceivedMessages());
        
        // 验证客户端2是否收到私聊消息
        assert client2.getReceivedMessages().stream()
                .anyMatch(msg -> msg.contains(message)) : "私聊消息接收失败";
        
        log.info("私聊消息测试通过");
    }

    /**
     * 测试通过REST API发送广播消息
     */
    @Test
    public void testRestApiBroadcast() throws InterruptedException {
        log.info("测试REST API发送广播消息...");
        
        // 通过REST API发送广播消息
        String message = "REST API广播消息_" + RandomUtil.randomString(6);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("message", message);
        String result = HttpUtil.post("http://localhost:" + port + "/websocket/broadcast", paramMap);
        
        log.info("REST API返回结果: {}", result);
        
        // 等待消息传输
        Thread.sleep(1000);
        
        // 验证客户端是否收到广播消息
        assert client1.getReceivedMessages().stream()
                .anyMatch(msg -> msg.contains(message)) : "客户端1未收到REST API发送的广播消息";
        assert client2.getReceivedMessages().stream()
                .anyMatch(msg -> msg.contains(message)) : "客户端2未收到REST API发送的广播消息";
        
        log.info("REST API广播消息测试通过");
    }

    /**
     * 测试通过REST API发送私聊消息
     */
    @Test
    public void testRestApiPrivateMessage() throws InterruptedException {
        log.info("测试REST API发送私聊消息...");
        
        // 通过REST API发送私聊消息
        String message = "REST API私聊消息_" + RandomUtil.randomString(6);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("message", message);
        paramMap.put("fromUserId", "apiUser");
        String result = HttpUtil.post("http://localhost:" + port + "/websocket/sendTo/testUser2", paramMap);
        
        log.info("REST API返回结果: {}", result);
        
        // 等待消息传输
        Thread.sleep(1000);
        
        // 验证客户端2是否收到私聊消息
        assert client2.getReceivedMessages().stream()
                .anyMatch(msg -> msg.contains(message)) : "客户端2未收到REST API发送的私聊消息";
        
        log.info("REST API私聊消息测试通过");
    }

    /**
     * 测试用WebSocket客户端实现类
     */
    public class TestWebSocketClient extends WebSocketClient {
        private final String clientName;
        private final StringBuilder receivedMessages = new StringBuilder();

        public TestWebSocketClient(URI serverUri, String clientName) {
            super(serverUri);
            this.clientName = clientName;
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            log.info("{}连接已打开", clientName);
        }

        @Override
        public void onMessage(String message) {
            log.info("{}收到消息: {}", clientName, message);
            receivedMessages.append(message).append("\n");
            latch.countDown();
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            log.info("{}连接已关闭, code={}, reason={}, remote={}", clientName, code, reason, remote);
        }

        @Override
        public void onError(Exception ex) {
            log.error("{}发生错误: {}", clientName, ex.getMessage(), ex);
        }

        /**
         * 发送广播消息
         */
        public void sendBroadcastMessage(String content) {
            String message = String.format("{\"type\":\"broadcast\",\"content\":\"%s\"}", content);
            send(message);
            log.info("{}发送广播消息: {}", clientName, content);
        }

        /**
         * 发送私聊消息
         */
        public void sendPrivateMessage(String targetId, String content) {
            String message = String.format("{\"type\":\"chat\",\"targetId\":\"%s\",\"content\":\"%s\"}", targetId, content);
            send(message);
            log.info("{}发送私聊消息给{}: {}", clientName, targetId, content);
        }

        /**
         * 获取接收到的所有消息
         */
        public String getReceivedMessages() {
            return receivedMessages.toString();
        }
    }
} 