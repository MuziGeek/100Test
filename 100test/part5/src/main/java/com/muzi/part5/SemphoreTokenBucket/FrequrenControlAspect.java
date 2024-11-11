package com.muzi.part5.SemphoreTokenBucket;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;

@Slf4j
@Aspect
@Component
@Order(0)
public class FrequrenControlAspect {

    @Around("@annotation(com.muzi.part5.SemphoreTokenBucket.FrequencyControl)||@annotation(com.muzi.part5.SemphoreTokenBucket.FrequncyControContainer)")
    public  Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method =((MethodSignature)joinPoint.getSignature()).getMethod();
        FrequencyControl[] annotationsByType = method.getAnnotationsByType(FrequencyControl.class);
        HashMap<String, FrequencyControl> keyMap = new HashMap<>();
        boolean flag = true;
         List<Semaphore> semaphores = new ArrayList<>();
        for (int i =0;i<annotationsByType.length;i++){
            FrequencyControl frequencyControl = annotationsByType[i];
            Semaphore semaphore = new Semaphore(frequencyControl.permits());
            semaphores.add(semaphore);
            boolean b = semaphore.tryAcquire(1, frequencyControl.timeout(), frequencyControl.unit());
            log.info("创建信号量成功");
            if (!b){
                flag=false;
            }

        }
        if (flag){
            try {
                return joinPoint.proceed();
            } finally {
                //这里一定不要漏掉了，令牌用完了，要还回去
                for (Semaphore semaphore:
                semaphores) {
                    semaphore.release();
                }

            }
        }else {
            return "系统繁忙，请稍后重试";
        }

    }
}
