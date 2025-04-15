package com.muzi.part5.LeakyBucket;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface LeakyBucketRateLimit {

    /**
     * 桶的容量
     *
     * @return
     */
    int capacity();

    /**
     * 漏斗的速率，单位通常是秒
     *
     * @return
     */
    int leakRate();
}