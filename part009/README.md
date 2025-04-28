# 动态线程池实现

## 项目背景
在高并发系统中，线程池是管理线程资源的重要组件。传统的线程池配置通常是静态的，无法根据系统负载动态调整。本项目实现了一个动态线程池管理系统，支持运行时动态调整线程池参数，提高系统资源利用率和响应能力。

## 解决的问题
1. 线程池参数静态配置问题
2. 系统负载变化时的资源利用效率
3. 线程池监控和管理需求
4. 多线程池统一管理需求

## 项目结构
```
part009/
├── src/main/java/com/muzi/
│   ├── comm/
│   │   ├── ThreadPoolManager.java      # 线程池管理器
│   │   ├── ThreadPoolInfo.java         # 线程池信息类
│   │   ├── ThreadPoolChange.java       # 线程池参数变更类
│   │   └── ResizeLinkedBlockingQueue.java  # 可调整大小的阻塞队列
│   ├── controller/
│   │   └── ThreadPoolManagerController.java # 线程池管理接口
│   └── config/
│       └── ThreadPoolConfig.java       # 线程池配置类
```

## 核心功能实现

### 1. 线程池管理器
```java
public class ThreadPoolManager {
    // 线程池映射表
    private static Map<String, ThreadPoolTaskExecutor> threadPoolMap = new ConcurrentHashMap<>(16);
    
    // 创建线程池
    public static ThreadPoolTaskExecutor createThreadPool(String threadPoolName) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadPoolName);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        threadPoolMap.put(threadPoolName, executor);
        return executor;
    }
    
    // 修改线程池参数
    public static void changeThreadPool(String threadPoolName, ThreadPoolChange threadPoolChange) {
        ThreadPoolTaskExecutor executor = threadPoolMap.get(threadPoolName);
        if (executor != null) {
            executor.setCorePoolSize(threadPoolChange.getCorePoolSize());
            executor.setMaxPoolSize(threadPoolChange.getMaxPoolSize());
            executor.setQueueCapacity(threadPoolChange.getQueueCapacity());
        }
    }
}
```

### 2. 可调整大小的阻塞队列
```java
public class ResizeLinkedBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E> {
    private final AtomicInteger count = new AtomicInteger();
    private final ReentrantLock takeLock = new ReentrantLock();
    private final Condition notEmpty = takeLock.newCondition();
    private final ReentrantLock putLock = new ReentrantLock();
    private final Condition notFull = putLock.newCondition();
    
    // 动态调整队列容量
    public void resizeCapacity(int newCapacity) {
        putLock.lock();
        try {
            if (newCapacity < count.get()) {
                throw new IllegalArgumentException("New capacity cannot be less than current size");
            }
            this.capacity = newCapacity;
            notFull.signalAll();
        } finally {
            putLock.unlock();
        }
    }
}
```

### 3. 线程池信息类
```java
public class ThreadPoolInfo {
    private String name;
    private int corePoolSize;
    private int maxPoolSize;
    private int activeCount;
    private int queueCapacity;
    private int queueSize;
    private int completedTaskCount;
    private int rejectedCount;
}
```

## 关键特性

### 1. 动态参数调整
- 支持运行时调整核心线程数
- 支持运行时调整最大线程数
- 支持运行时调整队列容量
- 参数变更实时生效

### 2. 线程池监控
- 实时监控线程池状态
- 统计任务执行情况
- 记录拒绝策略触发次数
- 监控队列使用情况

### 3. 多线程池管理
- 统一管理多个线程池
- 支持线程池命名
- 独立配置每个线程池
- 统一监控接口

### 4. 安全机制
- 参数变更原子性保证
- 队列容量调整安全控制
- 线程池状态一致性维护
- 异常情况处理机制

## 使用示例

### 1. 创建线程池
```java
// 创建名为"test-pool"的线程池
ThreadPoolTaskExecutor executor = ThreadPoolManager.createThreadPool("test-pool");
```

### 2. 提交任务
```java
// 提交任务到线程池
executor.execute(() -> {
    // 任务逻辑
});
```

### 3. 调整线程池参数
```java
ThreadPoolChange change = new ThreadPoolChange();
change.setName("test-pool");
change.setCorePoolSize(10);
change.setMaxPoolSize(20);
change.setQueueCapacity(1000);
ThreadPoolManager.changeThreadPool("test-pool", change);
```

### 4. 获取线程池信息
```java
// 获取所有线程池信息
List<ThreadPoolInfo> infoList = ThreadPoolManager.getThreadPoolInfoList();
```

## 技术要点

### 1. 线程池参数动态调整
- 核心线程数调整机制
- 最大线程数调整机制
- 队列容量动态调整
- 参数变更原子性保证

### 2. 线程池状态监控
- 活动线程数统计
- 队列使用情况监控
- 任务完成数统计
- 拒绝策略触发统计

### 3. 并发安全保证
- 使用ConcurrentHashMap存储线程池
- 参数变更加锁保护
- 队列操作原子性保证
- 状态一致性维护

### 4. 性能优化
- 线程池参数合理配置
- 队列容量动态调整
- 拒绝策略优化
- 资源利用效率提升

## 注意事项
1. 参数调整需要考虑系统负载
2. 队列容量调整要保证数据安全
3. 监控数据要及时清理
4. 异常情况要有合适的处理机制

## 未来优化方向
1. 支持更多线程池参数动态调整
2. 增加自适应参数调整机制
3. 提供更丰富的监控指标
4. 优化资源利用效率 