package com.muzi.part5.TokenBucket;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface TokenBucketRateLimit {
    /**
     * 产生令牌的速率(xx 个/秒)
     *
     * @return
     */
    double permitsPerSecond();
}