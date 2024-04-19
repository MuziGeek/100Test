package com.muzi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class SimpleBatchTask {
    public static void main(String[] args) {
        batchTaskTest();
    }

    public static void batchTaskTest(){
        long startTime =System.currentTimeMillis();
        List<String> messgList = new ArrayList<>();
        for (int i = 0; i <50 ; i++) {
            messgList.add("短信-"+i);
        }
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(messgList.size());
        for (String mess:
             messgList) {

            executorService.execute(()->{
                try {
                    //交个线程池处理任务
                    disposeTask(mess);
                } finally {
                    //处理完成后调用 countDownLatch.countDown()
                    countDownLatch.countDown();
                }

            });

        }
        try {
            //阻塞当前线程池
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("任务处理完毕,耗时(ms):" + (System.currentTimeMillis() - startTime));
        executorService.shutdown();

    }
    public static void disposeTask(String task) {
        System.out.println(String.format("【%s】发送成功", task));
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
