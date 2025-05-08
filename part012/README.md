# Part012 账户充值系统

## 项目概述
这是一个基于Spring Boot的账户充值系统，主要实现了账户余额管理和充值订单处理功能。系统采用幂等性设计，确保在高并发场景下的数据一致性。

## 核心功能
- 账户余额管理
- 充值订单处理
- 幂等性控制
- 事务管理

## 技术架构
- 框架：Spring Boot 2.7.13
- 持久层：MyBatis-Plus 3.5.3
- 数据库：MySQL
- 工具库：
  - Hutool 5.8.2
  - Commons IO 2.11.0
  - Commons Lang3
  - Commons Collections4 4.4

## 核心组件分析

### 1. 数据模型
#### 1.1 账户实体(AccountPO)
```java
@TableName("t_account")
@Data
public class AccountPO {
    private String id;            // 账户ID
    private String name;          // 账户名称
    private BigDecimal balance;   // 账户余额
}
```

#### 1.2 幂等性记录(IdempotentPO)
```java
@TableName("t_idempotent")
@Data
public class IdempotentPO {
    private String id;            // 主键
    private String idempotentKey; // 幂等键
}
```

### 2. 服务层实现

#### 2.1 账户服务(AccountServiceImpl)
```java
@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountMapper accountMapper;
    
    @Override
    public void balanceAdd(String accountId, BigDecimal price) {
        // 更新账户余额
        int i = accountMapper.balanceAdd(accountId, price);
        if (i != 1) {
            throw new RuntimeException("更新余额失败");
        }
    }
}
```

#### 2.2 幂等性服务(IdempotentServiceImpl)
```java
@Service
public class IdempotentServiceImpl implements IdempotentService {
    @Autowired
    private IdempotentMapper idempotentMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void idempotent(String idempotentKey, Runnable runnable) {
        // 检查幂等键是否存在
        IdempotentPO idempotentPO = idempotentMapper.selectOne(
            new LambdaQueryWrapper<IdempotentPO>()
                .eq(IdempotentPO::getIdempotentKey, idempotentKey)
        );
        
        if (idempotentPO != null) {
            return;
        }
        
        // 执行业务逻辑
        runnable.run();
        
        // 记录幂等键
        idempotentPO = new IdempotentPO();
        idempotentPO.setId(UUID.randomUUID().toString());
        idempotentPO.setIdempotentKey(idempotentKey);
        idempotentMapper.insert(idempotentPO);
    }
}
```

## 技术特点

### 1. 数据访问层
- 使用MyBatis-Plus框架
- 采用BaseMapper简化CRUD操作
- 自定义SQL方法支持复杂查询

### 2. 事务管理
- 使用Spring的@Transactional注解
- 配置了异常回滚机制

### 3. 幂等性设计
- 使用唯一键约束确保幂等性
- 采用业务ID+类型的方式生成幂等键
- 事务保证原子性

### 4. 代码规范
- 使用Lombok简化代码
- 遵循接口设计原则
- 统一的异常处理机制

## 安全性考虑
1. 使用事务确保数据一致性
2. 幂等性控制防止重复操作
3. 异常处理机制保证系统稳定性

## 可优化点
1. 可以考虑添加更多的业务校验
2. 可以增加日志记录
3. 可以添加缓存机制提升性能
4. 可以考虑添加更多的异常处理场景

## API接口说明

### 1. 充值回调接口
```java
@PostMapping("/rechargeCallBack")
public boolean rechargeCallBack(@RequestParam("rechargeId") String rechargeId) {
    return rechargeService.rechargeCallBack(rechargeId);
}
```

### 2. 账户余额查询
```java
@GetMapping("/accountInfo")
public AccountPO accountInfo(@RequestParam("accountId") String accountId) {
    return accountService.getById(accountId);
}
```

## 使用示例
```java
// 充值回调处理
@PostMapping("/recharge")
public void recharge(@RequestParam("rechargeId") String rechargeId) {
    rechargeService.rechargeCallBack(rechargeId);
}
```

## 注意事项
1. 所有涉及金额的操作必须使用BigDecimal
2. 关键业务操作必须使用事务
3. 需要幂等性控制的接口必须使用IdempotentService
4. 异常处理必须统一使用ServiceExceptionUtils

## 总结
本模块实现了一个完整的账户充值系统，包含了必要的幂等性控制和事务管理机制，适合在高并发场景下使用。系统设计合理，代码结构清晰，遵循了良好的设计规范。 