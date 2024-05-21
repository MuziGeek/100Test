package com.muzi.part11.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * job测试案例
 * <b>description</b>： Java高并发、微服务、性能优化实战案例100讲，视频号：程序员路人，源码 & 文档 & 技术支持，请加个人微信号：itsoku <br>
 * <b>time</b>：2024/4/3 0:13 <br>
 * <b>author</b>：ready likun_557@163.com
 */
@Slf4j
@Component
public class Job1 {
    public void execute() {
        log.info("job1");
    }
}
