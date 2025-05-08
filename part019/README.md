# 订单状态机最佳实践

## 模块说明
本模块演示了订单状态机的两种实现方式及其优化，包括：
- 传统if-else判断方式
- 状态机模式实现

## 技术架构
- JDK 8+
- Lombok
- 状态机模式

## 核心组件分析

### 1. 订单状态枚举
```java
public enum OrderStatus {
    INIT(0,"待支付"),
    PAID(100,"已付款"),
    SHIPPED(200,"已发货"),
    FINISHED(300,"已结束");
    
    private int status;
    private String desc;
    
    // 构造函数和getter方法
}
```
- 特点：使用枚举定义订单状态
- 优势：类型安全，避免魔法数字
- 扩展性：易于添加新状态

### 2. 订单状态转换动作
```java
public enum OrderStatusChanegeAction {
    PAY,    // 支付
    SHIP,   // 发货
    DELIVER;// 确认收货
}
```
- 特点：定义状态转换的触发动作
- 优势：清晰表达业务含义
- 扩展性：易于添加新动作

### 3. 订单状态转换定义
```java
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class OrderStatusTransition {
    // 当前状态
    private OrderStatus fromStatus;
    // 动作
    private OrderStatusChanegeAction action;
    // 目标状态
    private OrderStatus toStatus;
}
```
- 特点：定义状态转换规则
- 优势：将状态转换规则与业务逻辑分离
- 扩展性：易于添加新规则

## 实现方式对比

### 1. 传统if-else实现
```java
public class OrderService {
    public void pay(OrderModel orderModel) {
        // 验证订单状态
        if (!Objects.equals(orderModel.getStatus(), OrderStatus.INIT.getStatus())) {
            throw new RuntimeException("订单状态不支持当前操作");
        }
        // 将订单状态置为已支付状态
        orderModel.setStatus(OrderStatus.PAID.getStatus());
        // 其他操作...
    }
    
    public void ship(OrderModel orderModel) {
        // 验证订单状态
        if (!Objects.equals(orderModel.getStatus(), OrderStatus.PAID.getStatus())) {
            throw new RuntimeException("订单状态不支持当前操作");
        }
        // 将订单状态置为已发货状态
        orderModel.setStatus(OrderStatus.SHIPPED.getStatus());
        // 其他操作...
    }
    
    // 其他方法...
}
```
- 优点：直观，易于理解
- 缺点：代码重复，不易扩展，状态转换逻辑与业务逻辑混合

### 2. 状态机模式实现
```java
public class OrderServiceNew {
    // 订单状态转换列表
    public static List<OrderStatusTransition> orderStatusTransitionList = new ArrayList<>();
    
    static {
        // 初始化状态转换规则
        orderStatusTransitionList.add(OrderStatusTransition.builder()
                .fromStatus(OrderStatus.INIT)
                .action(OrderStatusChanegeAction.PAY)
                .toStatus(OrderStatus.PAID).build());
        // 其他规则...
    }
    
    // 触发订单状态转换
    private void statusTransition(OrderModel orderModel, OrderStatusChanegeAction action) {
        OrderStatus fromStatus = OrderStatus.get(orderModel.getStatus());
        Optional<OrderStatusTransition> first = orderStatusTransitionList.stream()
                .filter(orderStatusTransition -> 
                    orderStatusTransition.getFromStatus().equals(fromStatus) && 
                    orderStatusTransition.getAction().equals(action))
                .findFirst();
        if (!first.isPresent()) {
            throw new RuntimeException("订单状态不支持当前操作");
        }
        OrderStatusTransition orderStatusTransition = first.get();
        // 切换订单状态
        orderModel.setStatus(orderStatusTransition.getToStatus().getStatus());
    }
    
    // 业务方法
    public void pay(OrderModel orderModel) {
        // 订单状态转换
        this.statusTransition(orderModel, OrderStatusChanegeAction.PAY);
        // 其他操作...
    }
    
    // 其他方法...
}
```
- 优点：状态转换逻辑与业务逻辑分离，易于扩展
- 缺点：初始配置较复杂，需要理解状态机模式

## 最佳实践

### 1. 状态定义
- 使用枚举定义状态，避免魔法数字
- 为状态添加描述信息，提高可读性
- 状态值使用递增数字，便于扩展

### 2. 状态转换规则
- 将状态转换规则集中管理
- 使用Builder模式创建规则对象
- 规则定义清晰，易于维护

### 3. 状态转换逻辑
- 将状态转换逻辑与业务逻辑分离
- 使用Stream API查找匹配规则
- 统一异常处理

### 4. 业务方法
- 业务方法只关注业务逻辑
- 状态转换通过统一方法处理
- 代码简洁，易于维护

## 扩展功能

### 1. 状态转换前置/后置处理
```java
public class OrderStatusTransition {
    // 前置处理
    private Consumer<OrderModel> beforeTransition;
    // 后置处理
    private Consumer<OrderModel> afterTransition;
    
    // 其他属性...
}
```

### 2. 状态转换条件
```java
public class OrderStatusTransition {
    // 转换条件
    private Predicate<OrderModel> condition;
    
    // 其他属性...
}
```

### 3. 状态转换事件
```java
public class OrderStatusTransition {
    // 转换事件
    private String event;
    
    // 其他属性...
}
```

## 注意事项

### 1. 状态定义
- 状态值要有意义，便于理解
- 状态描述要准确，避免歧义
- 状态数量要适中，避免过多或过少

### 2. 状态转换规则
- 规则要完整，覆盖所有可能的转换
- 规则要一致，避免冲突
- 规则要清晰，易于维护

### 3. 状态转换逻辑
- 转换逻辑要统一，避免分散
- 转换逻辑要健壮，处理异常情况
- 转换逻辑要高效，避免性能问题

### 4. 业务方法
- 业务方法要简洁，只关注业务逻辑
- 业务方法要健壮，处理异常情况
- 业务方法要高效，避免性能问题 