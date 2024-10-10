package com.muzi.part5.Anno;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 固定窗口限流
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimiterSimpleWindow {
    /**
     * 限流
     * qps
     */

    int qps() default 2;

    /**
     * 超时时间
     * 默认为1
     */

    int timeout() default 1;


    /**
     * 时间单位 默认为秒
     */

    TimeUnit unit() default TimeUnit.SECONDS;
}
