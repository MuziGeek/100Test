package com.muzi.part5.SlidingWindow;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;


@Aspect
@Component
public class SlidingWindowRateLimitAspect {

    /**
     * 使用 ConcurrentHashMap 保存每个方法的请求时间戳队列
     */
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<Long>> REQUEST_TIMES_MAP = new ConcurrentHashMap<>();


    @Around("@annotation(com.muzi.part5.SlidingWindow.SlidingWindowRateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        com.muzi.part5.SlidingWindow.SlidingWindowRateLimit rateLimit = method.getAnnotation(com.muzi.part5.SlidingWindow.SlidingWindowRateLimit.class);

        // 允许的最大请求数
        int requests = rateLimit.maxRequest();
        // 滑动窗口的大小(秒)
        int timeWindow = rateLimit.timeWindow();

        // 获取方法名称字符串
        String methodName = method.toString();
        // 如果不存在当前方法的请求时间戳队列，则初始化一个新的队列
        ConcurrentLinkedQueue<Long> requestTimes = REQUEST_TIMES_MAP.computeIfAbsent(methodName,
                k -> new ConcurrentLinkedQueue<>());

        // 当前时间
        long currentTime = System.currentTimeMillis();
        // 计算时间窗口的开始时间戳
        long thresholdTime = currentTime - TimeUnit.SECONDS.toMillis(timeWindow);

        // 这一段代码是滑动窗口限流算法中的关键部分，其功能是移除当前滑动窗口之前的请求时间戳。这样做是为了确保窗口内只保留最近时间段内的请求记录。
        // requestTimes.isEmpty() 是检查队列是否为空的条件。如果队列为空，则意味着没有任何请求记录，不需要进行移除操作。
        // requestTimes.peek() < thresholdTime 是检查队列头部的时间戳是否早于滑动窗口的开始时间。如果是，说明这个时间戳已经不在当前的时间窗口内，应当被移除。
        while (!requestTimes.isEmpty() && requestTimes.peek() < thresholdTime) {
            // 移除队列头部的过期时间戳
            requestTimes.poll();
        }

        // 检查当前时间窗口内的请求次数是否超过限制
        if (requestTimes.size() < requests) {
            // 未超过限制，记录当前请求时间
            requestTimes.add(currentTime);
            return joinPoint.proceed();
        } else {
            // 超过限制，抛出限流异常
            return "服务繁忙，请稍后重试";
        }
    }


}