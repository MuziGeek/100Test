package com.muzi.part5.counter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CounterRateLimit {


    /**
     * 请求数量
     *
     * @return
     */
    int maxRequest();


    /**
     * 时间窗口， 单位秒
     *
     * @return
     */
    int timeWindow();


}