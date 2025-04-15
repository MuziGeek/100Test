package com.muzi.part5.LeakyBucket;

import com.muzi.part5.LeakyBucket.LeakyBucketRateLimit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


@Aspect
@Component
public class LeakyBucketRateLimitAspect {


    @Around("@annotation(com.muzi.part5.LeakyBucket.LeakyBucketRateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LeakyBucketRateLimit leakyBucketRateLimit = method.getAnnotation(LeakyBucketRateLimit.class);

        int capacity = leakyBucketRateLimit.capacity();
        int leakRate = leakyBucketRateLimit.leakRate();

        // 方法签名作为唯一标识
        String methodKey = method.toString();

        LeakyBucketLimiter limiter = LeakyBucketLimiter.createLimiter(methodKey, capacity, leakRate);
        if (!limiter.tryAcquire()) {
            // 超过限制，抛出限流异常
            return "服务繁忙，请稍后重试";
        }

        return joinPoint.proceed();
    }
}