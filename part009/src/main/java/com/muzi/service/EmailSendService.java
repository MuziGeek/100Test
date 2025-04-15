package com.muzi.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import com.muzi.utils.TaskDisposeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;


@Component
@Slf4j
public class EmailSendService implements ApplicationListener<ApplicationStartedEvent>, DisposableBean {
    private boolean stop = false;
    @Autowired
    private ThreadPoolTaskExecutor emailThreadPool;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        Thread thread = new Thread(() -> {
            // 处理邮件发送任务
            this.disposeSendEmailTask();
        }, "EmailSendService");
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * 处理邮件发送任务
     *
     * @throws InterruptedException
     */
    public void disposeSendEmailTask() {
        //循环拉取需要发送的邮件，使用线程池进行发送
        while (!stop) {
            try {
                List<String> emailListTask = this.getWaitSendEmailList();
                //使用线程池处理邮件发送
                TaskDisposeUtils.dispose(emailListTask, this::sendEmail, this.emailThreadPool);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private void sendEmail(String email) {
        try {
            //模拟邮件发送
            TimeUnit.SECONDS.sleep(1);
            log.info("模拟邮件发送，发送邮件。。。。，{}", email);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    public List<String> getWaitSendEmailList() {
        int count = new Random().nextInt(500);
        if (count == 0) {
            count = 100;
        }
        List<String> email = new ArrayList<>(count);
        for (int i = 1; i <= count; i++) {
            email.add("邮件-" + i);
        }
        return email;
    }


    @Override
    public void destroy() throws Exception {
        this.stop = true;
    }
}
