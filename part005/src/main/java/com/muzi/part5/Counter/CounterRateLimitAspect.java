package com.muzi.part5.Counter;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Aspect
@Component
public class CounterRateLimitAspect {

    // 存储每个方法对应的请求次数
    private Map<String, AtomicInteger> REQUEST_COUNT = new ConcurrentHashMap<>();

    // 存储每个方法的时间戳
    private Map<String, Long> REQUEST_TIMESTAMP = new ConcurrentHashMap<>();

    /**
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("@annotation(com.muzi.part5.Counter.CounterRateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint) throws Throwable {

        // 获取注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CounterRateLimit annotation = method.getAnnotation(CounterRateLimit.class);

        // 获取注解的参数
        int maxRequest = annotation.maxRequest();
        long timeWindowInMillis = TimeUnit.SECONDS.toMillis(annotation.timeWindow());

        // 获取方法名
        String methodName = method.toString();

        // 初始化计数器和时间戳
        AtomicInteger count = REQUEST_COUNT.computeIfAbsent(methodName, x -> new AtomicInteger(0));
        long startTime = REQUEST_TIMESTAMP.computeIfAbsent(methodName, x -> System.currentTimeMillis());

        // 获取当前时间
        long currentTimeMillis = System.currentTimeMillis();

        // 判断： 如果当前时间超出时间窗口，则重置
        if (currentTimeMillis - startTime > timeWindowInMillis) {
            count.set(0);
            REQUEST_TIMESTAMP.put(methodName, currentTimeMillis);
        }

        // 原子的增加计数器并检查其值
        if (count.incrementAndGet() > maxRequest) {
            // 如果超出最大请求次数，递减计数器，并报错
            count.decrementAndGet();
            return "服务繁忙，请稍后重试";
        }

        // 方法原执行
        return joinPoint.proceed();
    }
}
