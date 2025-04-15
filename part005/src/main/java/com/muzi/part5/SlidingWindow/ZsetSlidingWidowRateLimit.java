package com.muzi.part5.SlidingWindow;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ZsetSlidingWidowRateLimit {
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
