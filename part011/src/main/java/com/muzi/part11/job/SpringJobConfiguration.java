package com.muzi.part11.job;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;


@Configuration
public class SpringJobConfiguration {
    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        //线程池大小
        threadPoolTaskScheduler.setPoolSize(100);
        //线程名称前缀
        threadPoolTaskScheduler.setThreadNamePrefix("taskExecutor-");
        //等待时长
        threadPoolTaskScheduler.setAwaitTerminationSeconds(60);
        //关闭任务线程时是否等待当前被调度的任务完成
        threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        return threadPoolTaskScheduler;
    }
}
