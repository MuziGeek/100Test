package com.muzi.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Consumer;


public class TaskDisposeUtils {
    /**
     * 使用线程池批处理文件，当所有任务处理完毕后才会返回
     *
     * @param taskList 任务列表
     * @param consumer 处理任务的方法
     * @param executor 线程池
     * @param <T>
     * @throws InterruptedException
     */
    public static <T> void dispose(List<T> taskList, Consumer<? super T> consumer, Executor executor) throws InterruptedException {
        if (taskList == null || taskList.size() == 0) {
            return;
        }
        Objects.nonNull(consumer);

        CountDownLatch countDownLatch = new CountDownLatch(taskList.size());
        for (T item : taskList) {
            executor.execute(() -> {
                try {
                    consumer.accept(item);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
    }

    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        //任务列表
        List<String> taskList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            taskList.add("短信-" + i);
        }

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        //调用工具类批处理任务
        TaskDisposeUtils.dispose(taskList, TaskDisposeUtils::disposeTask, executorService);

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
