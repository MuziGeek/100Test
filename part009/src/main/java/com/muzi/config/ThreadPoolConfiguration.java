package com.muzi.config;


import com.muzi.comm.ThreadPoolManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@Configuration(proxyBeanMethods = false)
public class ThreadPoolConfiguration {
    /**
     * 发送邮件用到的线程池
     *
     * @return
     */
    @Bean
    public ThreadPoolTaskExecutor emailThreadPool() {
        return ThreadPoolManager.newThreadPool("emailThreadPool", 10, 20, 1000);
    }

    /**
     * 发送短信用到的线程池
     *
     * @return
     */
    @Bean
    public ThreadPoolTaskExecutor smsThreadPool() {
        return ThreadPoolManager.newThreadPool("smsThreadPool");
    }
}
