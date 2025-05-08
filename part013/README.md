# Part013 统一响应与异常处理

## 项目概述
这是一个基于Spring Boot的Web应用示例，主要展示了如何实现统一的响应格式和全局异常处理机制。该模块提供了一个完整的异常处理框架，包括业务异常、参数校验异常和系统异常的统一处理，以及标准化的API响应格式。

## 核心功能
- 统一响应格式封装
- 全局异常处理
- 参数校验
- 业务异常定义与处理

## 技术架构
- 框架：Spring Boot 2.7.13
- 参数校验：Spring Validation
- 工具库：
  - Hutool 5.8.2
  - Commons IO 2.11.0
  - Commons Lang3
  - Commons Collections4 4.4

## 核心组件分析

### 1. 统一响应格式(Result)
```java
public class Result<T> {
    private boolean success;  // 请求是否处理成功
    public T data;           // 业务数据
    private String msg;      // 提示消息
    private String code;     // 错误编码
    
    // 构造函数和getter/setter方法
}
```

### 2. 业务异常定义(BusinessException)
```java
public class BusinessException extends RuntimeException {
    private String code;  // 错误编码
    
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }
    
    // 其他构造函数和getter/setter方法
}
```

### 3. 全局异常处理器(GlobalExceptionHandler)
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    // 处理业务异常
    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException e, HttpServletRequest request) {
        logger.info("请求：{}，发生异常：{}", request.getRequestURL(), e.getMessage(), e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }
    
    // 处理参数校验异常
    @ExceptionHandler(java.net.BindException.class)
    public Result handleBindException(BindException e, HttpServletRequest request) {
        logger.info("请求：{}，发生异常：{}", request.getRequestURL(), e.getMessage(), e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return ResultUtils.error(message);
    }
    
    // 处理其他异常
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e, HttpServletRequest request) {
        logger.info("请求：{}，发生异常：{}", request.getRequestURL(), e.getMessage(), e);
        return ResultUtils.error(ErrorCode.SERVER_ERROR, "系统异常，请稍后重试");
    }
}
```

### 4. 参数校验示例(UserRegisterRequest)
```java
public class UserRegisterRequest {
    @NotBlank(message = "用户名不能为空")
    private String userName;
    
    @NotBlank(message = "密码不能为空")
    private String password;
    
    // getter/setter方法
}
```

## 技术特点

### 1. 统一响应格式
- 使用泛型支持不同类型的响应数据
- 包含成功标志、数据、消息和错误码
- 提供工具类简化响应对象的创建

### 2. 异常处理机制
- 分层处理不同类型的异常
- 业务异常携带错误码和消息
- 参数校验异常提取校验失败信息
- 系统异常统一返回500错误码

### 3. 参数校验
- 使用Spring Validation框架
- 通过注解实现参数校验
- 统一的校验异常处理

### 4. 日志记录
- 异常处理过程中记录详细日志
- 包含请求URL和异常信息

## API接口说明

### 1. 测试接口
```java
@GetMapping("/hello")
public Result<String> hello() {
    return ResultUtils.success("test100");
}
```

### 2. 登录接口
```java
@GetMapping("/login")
public Result<String> login(String name) {
    if (!"木子".equals(name)) {
        throw BusinessExceptionUtils.businessException("1001", "用户名错误");
    } else {
        return ResultUtils.success("登录成功");
    }
}
```

### 3. 用户注册接口
```java
@PostMapping("/userRegister")
public Result<Void> userRegister(@Validated @RequestBody UserRegisterRequest req) {
    return ResultUtils.success();
}
```

## 使用示例

### 1. 成功响应
```java
// 返回成功，无数据
return ResultUtils.success();

// 返回成功，带数据
return ResultUtils.success("操作成功");
```

### 2. 错误响应
```java
// 返回错误，带消息
return ResultUtils.error("用户名不能为空");

// 返回错误，带错误码和消息
return ResultUtils.error("1001", "用户名错误");
```

### 3. 抛出业务异常
```java
// 抛出业务异常
throw BusinessExceptionUtils.businessException("1001", "用户名错误");
```

## 注意事项
1. 所有API响应必须使用Result包装
2. 业务异常必须使用BusinessException
3. 参数校验必须使用@Validated注解
4. 异常处理必须通过全局异常处理器

## 总结
本模块实现了一个完整的统一响应和异常处理框架，可以有效提高API的一致性和可维护性。通过统一的响应格式和全局异常处理，简化了开发流程，提高了代码质量，为大型应用提供了良好的基础架构。 