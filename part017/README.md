# Part017 链路追踪实现

## 项目概述
这是一个基于Spring Boot的Web应用示例，主要展示了如何实现请求链路追踪功能。该模块通过过滤器、AOP和ThreadLocal等技术，为每个请求生成唯一的追踪ID，并在日志和响应中携带该ID，便于问题排查和性能分析。

## 核心功能
- 请求链路追踪
- 统一响应格式
- 全局异常处理
- 请求耗时统计

## 技术架构
- 框架：Spring Boot 2.7.13
- AOP：Spring AOP
- 工具库：
  - Hutool 5.8.2
  - Commons IO 2.11.0
  - Commons Lang3
  - Commons Collections4 4.4

## 核心组件分析

### 1. 链路追踪工具(TraceUtils)
```java
public class TraceUtils {
    public static final String TRACE_ID = "traceId";
    public static ThreadLocal<String> traceIdThreadLocal = new ThreadLocal<>();
    
    public static String getTraceId() {
        return traceIdThreadLocal.get();
    }
    
    public static void setTraceId(String traceId) {
        traceIdThreadLocal.set(traceId);
        MDC.put(TRACE_ID, traceId);
    }
    
    public static void removeTraceId() {
        traceIdThreadLocal.remove();
        MDC.remove(TRACE_ID);
    }
}
```

### 2. 链路追踪过滤器(TraceFilter)
```java
@Order(Ordered.HIGHEST_PRECEDENCE)
@WebFilter(urlPatterns = "/**", filterName = "TraceFilter")
public class TraceFilter extends OncePerRequestFilter {
    public static Logger logger = LoggerFactory.getLogger(TraceFilter.class);
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String traceID = IdUtil.fastSimpleUUID();
        TraceUtils.setTraceId(traceID);
        long st = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long et = System.currentTimeMillis();
            logger.info("请求地址:{},耗时(ms):{}", request.getRequestURL().toString(), (et - st));
            TraceUtils.removeTraceId();
        }
    }
}
```

### 3. 响应追踪ID切面(ResultTraceIdAspect)
```java
@Aspect
@Order
public class ResultTraceIdAspect {
    @Pointcut("execution(* com.muzi..*Controller.*(..)) ||execution(* com.muzi.part17.web.GlobalExceptionHandler.*(..))")
    public void pointCut() {
    }
    
    @Around("pointCut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Object object = pjp.proceed();
        if (object instanceof Result) {
            ((Result<?>) object).setTraceId(TraceUtils.getTraceId());
        }
        return object;
    }
}
```

### 4. 统一响应格式(Result)
```java
public class Result<T> {
    private boolean success;  // 请求是否处理成功
    public T data;           // 业务数据
    private String msg;      // 提示消息
    private String code;     // 错误编码
    private String traceId;  // 链路追踪id
    
    // 构造函数和getter/setter方法
}
```

## 技术特点

### 1. 链路追踪实现
- 使用ThreadLocal存储追踪ID
- 使用MDC记录追踪ID到日志
- 通过过滤器为每个请求生成唯一ID
- 通过AOP将追踪ID注入到响应中

### 2. 请求耗时统计
- 在过滤器中记录请求开始和结束时间
- 计算请求处理耗时并记录到日志

### 3. 统一响应格式
- 使用泛型支持不同类型的响应数据
- 包含成功标志、数据、消息和错误码
- 携带链路追踪ID便于问题排查

### 4. 全局异常处理
- 分层处理不同类型的异常
- 业务异常携带错误码和消息
- 参数校验异常提取校验失败信息
- 系统异常统一返回500错误码

## API接口说明

### 1. 测试接口
```java
@GetMapping
public Result<String> hello() throws InterruptedException {
    logger.info("开始执行业务");
    TimeUnit.MILLISECONDS.sleep(500);
    logger.info("业务执行结束");
    return ResultUtils.success("111111");
}
```

### 2. 异常测试接口
```java
@GetMapping("/exception")
public Result<String> exception() throws InterruptedException {
    logger.info("开始执行业务");
    //这里模拟了一个错误，10/0，会报错
    System.out.println(10 / 0);
    logger.info("业务执行结束");
    return ResultUtils.success("22222");
}
```

## 使用示例

### 1. 查看请求追踪ID
```java
// 在日志中可以看到追踪ID
logger.info("开始执行业务");
// 输出: [traceId=abc123] 开始执行业务
```

### 2. 查看请求耗时
```java
// 在日志中可以看到请求耗时
// 输出: 请求地址:http://localhost:8080/,耗时(ms):500
```

### 3. 查看响应中的追踪ID
```json
// 响应结果
{
  "success": true,
  "data": "111111",
  "msg": null,
  "code": null,
  "traceId": "abc123"
}
```

## 注意事项
1. 过滤器必须设置最高优先级，确保最先执行
2. 必须及时清理ThreadLocal，避免内存泄漏
3. 异常处理必须通过全局异常处理器
4. 所有响应必须使用Result包装

## 总结
本模块实现了一个完整的请求链路追踪功能，可以有效提高系统的可观测性和问题排查效率。通过为每个请求生成唯一的追踪ID，并在日志和响应中携带该ID，可以快速定位问题、分析性能瓶颈，为大型应用提供了良好的监控基础。 