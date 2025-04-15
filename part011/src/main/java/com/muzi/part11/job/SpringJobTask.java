package com.muzi.part11.job;

import cn.hutool.core.util.ReflectUtil;
import com.muzi.part11.dto.Job;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.ScheduledFuture;


@Data
@Slf4j
public class SpringJobTask implements Runnable {
    private ScheduledFuture scheduledFuture;
    private Job job;
    private ApplicationContext applicationContext;

    public SpringJobTask(Job job, ApplicationContext applicationContext) {
        this.job = job;
        this.applicationContext = applicationContext;
    }

    @Override
    public void run() {
        //从spring容器中拿到bean，然后通过反射调用其需要执行的方法
        Object bean = this.applicationContext.getBean(this.job.getBeanName());
        ReflectUtil.invoke(bean, this.job.getBeanMethod());
    }

    public ScheduledFuture getScheduledFuture() {
        return scheduledFuture;
    }

    public void setScheduledFuture(ScheduledFuture scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    public Job getJob() {
        return job;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
