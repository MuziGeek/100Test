package com.muzi.part17.trace;

import com.muzi.part17.common.Result;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;


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
