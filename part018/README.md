# ThreadLocal 最佳实践

## 模块说明
本模块演示了ThreadLocal的三种实现方式及其使用场景，包括：
- 基础ThreadLocal
- InheritableThreadLocal
- TransmittableThreadLocal

## 技术架构
- JDK 8+
- Alibaba TTL (TransmittableThreadLocal)
- SLF4J 日志框架
- JUnit 5 测试框架

## 核心组件分析

### 1. 基础ThreadLocal
```java
ThreadLocal<String> userNameTL = new ThreadLocal<>();
```
- 特点：线程隔离，每个线程独立存储数据
- 使用场景：线程内数据传递
- 生命周期：与线程绑定，线程结束自动清理

### 2. InheritableThreadLocal
```java
InheritableThreadLocal<String> userNameItl = new InheritableThreadLocal<>();
```
- 特点：支持父子线程间数据传递
- 使用场景：需要在子线程中获取父线程数据
- 限制：不支持线程池场景

### 3. TransmittableThreadLocal
```java
TransmittableThreadLocal<String> userNameTtl = new TransmittableThreadLocal<>();
```
- 特点：支持线程池场景下的数据传递
- 使用场景：异步任务、线程池操作
- 优势：完整的线程间数据传递支持

## 使用示例

### 1. 基础ThreadLocal示例
```java
// 设置值
userNameTL.set("张三");

// 获取值
String value = userNameTL.get();

// 清理值
userNameTL.remove();
```

### 2. InheritableThreadLocal示例
```java
// 父线程设置值
userNameItl.set("张三");

// 创建子线程，自动继承父线程的值
new Thread(() -> {
    String value = userNameItl.get(); // 获取到"张三"
}).start();
```

### 3. TransmittableThreadLocal示例
```java
// 包装线程池
ExecutorService executorService = TtlExecutors.getTtlExecutorService(
    Executors.newFixedThreadPool(1)
);

// 设置值
userNameTtl.set("张三");

// 在线程池中执行任务，可以获取到值
executorService.execute(() -> {
    String value = userNameTtl.get(); // 获取到"张三"
});
```

## 最佳实践

### 1. 内存管理
- 及时调用remove()方法清理ThreadLocal变量
- 避免长时间持有ThreadLocal引用
- 使用try-finally确保清理操作执行

### 2. 线程池使用
- 优先使用TransmittableThreadLocal
- 正确包装线程池
- 注意值的传递时机

### 3. 性能优化
- 避免频繁创建ThreadLocal实例
- 合理使用ThreadLocal缓存
- 控制ThreadLocal变量数量

### 4. 异常处理
```java
try {
    userNameTL.set("value");
    // 业务逻辑
} finally {
    userNameTL.remove();
}
```

## 注意事项

### 1. 内存泄漏
- ThreadLocal变量未及时清理可能导致内存泄漏
- 特别是在线程池场景下需要特别注意

### 2. 线程安全
- ThreadLocal本身是线程安全的
- 存储的对象需要自行保证线程安全

### 3. 继承关系
- 普通ThreadLocal不支持父子线程间传递
- InheritableThreadLocal支持父子线程传递
- TransmittableThreadLocal支持所有场景

### 4. 线程池使用
- 必须使用TtlExecutors包装线程池
- 注意值的传递时机和范围
- 避免线程池复用导致的值污染

## 常见问题

### 1. 线程池中获取不到值
- 原因：普通ThreadLocal不支持线程池场景
- 解决：使用TransmittableThreadLocal并正确包装线程池

### 2. 子线程获取不到父线程的值
- 原因：使用了普通ThreadLocal
- 解决：使用InheritableThreadLocal或TransmittableThreadLocal

### 3. 内存泄漏
- 原因：ThreadLocal变量未及时清理
- 解决：使用try-finally确保清理，或使用ThreadLocal.withInitial()

## 扩展功能

### 1. 自定义ThreadLocal
```java
public class CustomThreadLocal<T> extends ThreadLocal<T> {
    @Override
    protected T initialValue() {
        return createDefaultValue();
    }
    
    private T createDefaultValue() {
        // 自定义初始化逻辑
        return null;
    }
}
```

### 2. 批量操作
```java
public class ThreadLocalUtils {
    public static void setBatch(ThreadLocal<?>... locals) {
        for (ThreadLocal<?> local : locals) {
            local.remove();
        }
    }
}
```

### 3. 值传递装饰器
```java
public class ThreadLocalDecorator {
    public static <T> T executeWithContext(ThreadLocal<T> local, T value, Runnable task) {
        T oldValue = local.get();
        try {
            local.set(value);
            task.run();
            return local.get();
        } finally {
            local.set(oldValue);
        }
    }
}
``` 

# ThreadLocal 最佳实践指南

## 目录
- [概述](#概述)
- [三种实现方式](#三种实现方式)
- [最佳实践](#最佳实践)
- [常见问题与解决方案](#常见问题与解决方案)
- [性能优化](#性能优化)
- [扩展功能](#扩展功能)

## 概述

ThreadLocal 是Java中实现线程隔离的重要机制，它为每个线程提供独立的变量副本，实现了线程的数据隔离。本指南涵盖了ThreadLocal的最佳实践、常见陷阱及其解决方案。

## 三种实现方式

### 1. 基础ThreadLocal
```java
ThreadLocal<String> userNameTL = new ThreadLocal<>();
```
- 特点：线程隔离，每个线程独立存储数据
- 使用场景：线程内数据传递
- 生命周期：与线程绑定，线程结束自动清理

### 2. InheritableThreadLocal
```java
InheritableThreadLocal<String> userNameItl = new InheritableThreadLocal<>();
```
- 特点：支持父子线程间数据传递
- 使用场景：需要在子线程中获取父线程数据
- 限制：不支持线程池场景

### 3. TransmittableThreadLocal
```java
TransmittableThreadLocal<String> userNameTtl = new TransmittableThreadLocal<>();
```
- 特点：支持线程池场景下的数据传递
- 使用场景：异步任务、线程池操作
- 优势：完整的线程间数据传递支持

## 最佳实践

### 1. 内存管理
```java
// 推荐使用静态final声明
private static final ThreadLocal<User> userHolder = new ThreadLocal<>();

// 使用try-finally确保清理
try {
    userHolder.set(value);
    // 业务逻辑
} finally {
    userHolder.remove();
}
```

### 2. 线程池使用
```java
// 使用TransmittableThreadLocal
TransmittableThreadLocal<String> context = new TransmittableThreadLocal<>();

// 包装线程池
ExecutorService executorService = TtlExecutors.getTtlExecutorService(
    Executors.newFixedThreadPool(10)
);

// 在线程池中使用
executorService.execute(() -> {
    String value = context.get();
    // 处理业务逻辑
});
```

### 3. 工具类封装
```java
public class UserContext {
    private static final ThreadLocal<User> USER_HOLDER = new ThreadLocal<>();
    
    public static void setUser(User user) {
        USER_HOLDER.set(user);
    }
    
    public static User getUser() {
        return USER_HOLDER.get();
    }
    
    public static void clear() {
        USER_HOLDER.remove();
    }
}
```

## 常见问题与解决方案

### 1. 内存泄漏问题
- 问题：ThreadLocal变量未及时清理
- 解决方案：
  ```java
  public class ThreadLocalScope implements AutoCloseable {
      private final ThreadLocal<?> threadLocal;
      
      public ThreadLocalScope(ThreadLocal<?> threadLocal, Object value) {
          this.threadLocal = threadLocal;
          threadLocal.set(value);
      }
      
      @Override
      public void close() {
          threadLocal.remove();
      }
  }
  ```

### 2. 线程池数据串流
- 问题：线程池复用导致数据污染
- 解决方案：
  ```java
  // 使用TransmittableThreadLocal
  TransmittableThreadLocal<String> context = new TransmittableThreadLocal<>();
  
  // 确保任务执行完清理数据
  executorService.execute(() -> {
      try {
          // 业务逻辑
      } finally {
          context.remove();
      }
  });
  ```

### 3. 父子线程数据传递
- 问题：子线程无法获取父线程数据
- 解决方案：
  ```java
  // 使用InheritableThreadLocal或TransmittableThreadLocal
  InheritableThreadLocal<String> context = new InheritableThreadLocal<>();
  ```

## 性能优化

### 1. 减少实例创建
```java
// 使用静态final声明
private static final ThreadLocal<DateFormat> dateFormatHolder = ThreadLocal.withInitial(
    () -> new SimpleDateFormat("yyyy-MM-dd")
);
```

### 2. 合理使用初始值
```java
public class OptimizedThreadLocal<T> extends ThreadLocal<T> {
    private final Supplier<T> supplier;
    
    public OptimizedThreadLocal(Supplier<T> supplier) {
        this.supplier = supplier;
    }
    
    @Override
    protected T initialValue() {
        return supplier.get();
    }
}
```

### 3. 批量操作工具
```java
public class ThreadLocalUtils {
    public static void clearAll(ThreadLocal<?>... locals) {
        for (ThreadLocal<?> local : locals) {
            local.remove();
        }
    }
}
```

## 扩展功能

### 1. 值传递装饰器
```java
public class ThreadLocalDecorator<T> {
    private final ThreadLocal<T> threadLocal;
    
    public void runWithValue(T value, Runnable task) {
        T old = threadLocal.get();
        try {
            threadLocal.set(value);
            task.run();
        } finally {
            if (old != null) {
                threadLocal.set(old);
            } else {
                threadLocal.remove();
            }
        }
    }
}
```

### 2. 线程池包装工具
```java
public class ThreadPoolUtils {
    public static ExecutorService wrapThreadPool(ExecutorService executor) {
        return TtlExecutors.getTtlExecutorService(executor);
    }
}
```

## 注意事项

1. 内存管理
   - 及时调用remove()方法清理ThreadLocal变量
   - 避免长时间持有ThreadLocal引用
   - 使用try-finally确保清理操作执行

2. 线程池使用
   - 优先使用TransmittableThreadLocal
   - 正确包装线程池
   - 注意值的传递时机

3. 安全考虑
   - ThreadLocal本身是线程安全的
   - 存储的对象需要自行保证线程安全
   - 避免存储敏感信息

4. 最佳实践
   - 使用static final定义ThreadLocal变量
   - 封装为工具类使用
   - 采用统一的命名规范
   - 做好注释和文档