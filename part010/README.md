# WebSocket示例项目

这是一个基于Spring Boot的WebSocket示例项目，展示了WebSocket的基本使用，包括：

- WebSocket服务端实现
- 客户端连接管理
- 私聊消息
- 广播消息
- REST API调用WebSocket发送消息

## 技术栈

- Spring Boot 2.7.13
- Spring WebSocket
- Hutool工具包
- Java-WebSocket (测试客户端)

## 项目结构

```
src/main/java/com/muzi/part010/
├── config/
│   └── WebSocketConfig.java           # WebSocket配置类
├── controller/
│   └── WebSocketController.java       # WebSocket控制器
├── websocket/
│   └── WebSocketServer.java           # WebSocket服务端
└── Part010Application.java            # 应用启动类
```

## 功能说明

1. **WebSocket服务端**:
   - 处理WebSocket连接、消息收发、断开
   - 维护在线用户列表
   - 支持私聊和广播消息

2. **消息格式**:
   ```json
   // 私聊消息
   {
     "type": "chat",
     "targetId": "接收用户ID",
     "content": "消息内容"
   }
   
   // 广播消息
   {
     "type": "broadcast",
     "content": "消息内容"
   }
   ```

3. **REST API**:
   - `/websocket/broadcast` - 发送广播消息
   - `/websocket/sendTo/{toUserId}` - 发送私聊消息

## 运行说明

1. 启动项目：
   ```bash
   mvn spring-boot:run
   ```

2. 访问WebSocket客户端页面：
   ```
   http://localhost:8010/websocket.html
   ```

3. 运行测试用例：
   ```bash
   mvn test
   ```

## 测试说明

测试类 `WebSocketTest` 包含以下测试用例：

- `testWebSocketConnection` - 测试WebSocket连接
- `testBroadcastMessage` - 测试广播消息
- `testPrivateMessage` - 测试私聊消息
- `testRestApiBroadcast` - 测试REST API发送广播消息
- `testRestApiPrivateMessage` - 测试REST API发送私聊消息 