package com.muzi.part5.AOP;

import com.muzi.part5.Anno.FrequencyControl;
import com.muzi.part5.Anno.RateLimiterSimpleWindow;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Aspect
@Component
@Order(0)
public class RateLimiterSimpleWindowAspect {
    private static long START_TIME = System.currentTimeMillis();
    // 计数器
    private static AtomicInteger REQ_COUNT = new AtomicInteger();


    @Around("@annotation(com.muzi.part5.Anno.RateLimiterSimpleWindow)")
    public  Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method =((MethodSignature)joinPoint.getSignature()).getMethod();
        RateLimiterSimpleWindow[] annotationsByType = method.getAnnotationsByType(RateLimiterSimpleWindow.class);
        HashMap<String, RateLimiterSimpleWindow> keyMap = new HashMap<>();
        boolean flag = true;
        for (int i =0;i<annotationsByType.length;i++){
            RateLimiterSimpleWindow RateLimiterSimpleWindow = annotationsByType[i];

            if ((System.currentTimeMillis() -START_TIME ) > RateLimiterSimpleWindow.timeout()*1000) {
                REQ_COUNT.set(0);
                START_TIME = System.currentTimeMillis();
            }
            flag=REQ_COUNT.incrementAndGet() <= RateLimiterSimpleWindow.qps();
        }

        if (flag){
            return joinPoint.proceed();
        } else {
        return "系统繁忙，请稍后重试";
        }
    }
}
