package com.muzi.part5.TokenBucket;

import com.google.common.util.concurrent.RateLimiter;
import com.muzi.part5.TokenBucket.TokenBucketRateLimit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;



@Aspect
@Component
public class TokenBucketRateLimitAspect {

    // 使用ConcurrentHashMap来存储每个方法的限流器
    private final ConcurrentHashMap<String, RateLimiter> limiters = new ConcurrentHashMap<>();

    // 环绕通知，用于在方法执行前后添加限流逻辑
    @Around("@annotation(com.muzi.part5.TokenBucket.TokenBucketRateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法签名，用于获取方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 根据方法签名获取方法对象
        Method method = signature.getMethod();
        // 从方法对象中获取限流注解
        TokenBucketRateLimit rateLimit = method.getAnnotation(TokenBucketRateLimit.class);

        // 获取注解中定义的每秒令牌数
        double permitsPerSecond = rateLimit.permitsPerSecond();

        // 获取方法名，作为限流器的唯一标识
        String methodName = method.toString();
        // 如果限流器缓存中没有该方法的限流器，则创建一个新的
        RateLimiter rateLimiter = limiters.computeIfAbsent(methodName, k -> RateLimiter.create(permitsPerSecond));

        // 尝试获取令牌，如果可以获取，则继续执行方法
        if (rateLimiter.tryAcquire()) {
            return joinPoint.proceed();
        } else {
            // 如果无法获取令牌，则抛出异常，告知用户请求过于频繁
            return "服务繁忙，请稍后重试";
        }
    }
}