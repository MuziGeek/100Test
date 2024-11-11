package com.muzi.part5.SemphoreTokenBucket;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Repeatable(FrequncyControContainer.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FrequencyControl {

    /**
     * 限流
     * 获取令牌数量 默认为1
     */

    int permits() default 1;

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
