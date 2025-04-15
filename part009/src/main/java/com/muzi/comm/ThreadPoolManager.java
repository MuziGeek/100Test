package com.muzi.comm;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;


public class ThreadPoolManager {
    private static Map<String, ThreadPoolTaskExecutor> threadPoolMap = new ConcurrentHashMap<String, ThreadPoolTaskExecutor>(16);

    private static int corePoolSize = 1;

    private static int maxPoolSize = Integer.MAX_VALUE;

    private static int queueCapacity = Integer.MAX_VALUE;

    private static int keepAliveSeconds = 60;

    /**
     * 创建新的线程池，如果线程池已经创建，返回已经创建的线程池，核心线程数{@link ThreadPoolManager#corePoolSize}，最大线程数{@link ThreadPoolManager#maxPoolSize}
     *
     * @param name 线程池名称
     * @return
     */
    public static ThreadPoolTaskExecutor newThreadPool(String name) {
        return newThreadPool(name, corePoolSize, maxPoolSize);
    }

    /**
     * 创建新的线程池，如果线程池已经创建，返回已经创建的线程池
     *
     * @param name         线程池名称
     * @param corePoolSize 核心线程数
     * @return
     */
    public static ThreadPoolTaskExecutor newThreadPool(String name, int corePoolSize) {
        return newThreadPool(name, corePoolSize, corePoolSize);
    }

    /**
     * 创建新的线程池，如果线程池已经创建，返回已经创建的线程池
     *
     * @param name         线程池名称
     * @param corePoolSize 核心线程数
     * @param maxPoolSize  最大线程数
     * @return
     */
    public static ThreadPoolTaskExecutor newThreadPool(String name, int corePoolSize, int maxPoolSize) {
        return newThreadPool(name, corePoolSize, maxPoolSize, queueCapacity, keepAliveSeconds, null, null);
    }

    /**
     * 创建新的线程池，如果线程池已经创建，返回已经创建的线程池
     *
     * @param name          线程池名称
     * @param corePoolSize  核心线程数
     * @param maxPoolSize   最大线程数
     * @param queueCapacity 队列大小
     * @return
     */
    public static ThreadPoolTaskExecutor newThreadPool(String name, int corePoolSize, int maxPoolSize, int queueCapacity) {
        return newThreadPool(name, corePoolSize, maxPoolSize, queueCapacity, keepAliveSeconds, null, null);
    }

    /**
     * 创建新的线程池，如果线程池已经创建，返回已经创建的线程池
     *
     * @param name                     线程池名称
     * @param corePoolSize             核心线程数
     * @param maxPoolSize              最大线程数
     * @param queueCapacity            队列大小
     * @param keepAliveSeconds         线程池存活时间（秒）
     * @param threadFactory            线程工厂
     * @param rejectedExecutionHandler 拒绝策略
     * @return
     */
    public static ThreadPoolTaskExecutor newThreadPool(String name, int corePoolSize, int maxPoolSize, int queueCapacity, int keepAliveSeconds, ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        return threadPoolMap.computeIfAbsent(name, threadGroupName -> {
            ThreadPoolTaskExecutor threadPoolExecutor = new ThreadPoolTaskExecutor() {
                //通过标识判断线程池是否已经创建
                private boolean initialized = false;

                @Override
                protected BlockingQueue<Runnable> createQueue(int queueCapacity) {
                    if (queueCapacity > 0) {
                        //重写动态线程池（可修改队列大小阻塞队列）
                        return new ResizeLinkedBlockingQueue<>(queueCapacity);
                    } else {
                        //容量为0
                        return new SynchronousQueue<>();
                    }
                }

                @Override
                public void setQueueCapacity(int queueCapacity) {
                    if (this.initialized && this.getThreadPoolExecutor() != null &&
                            this.getThreadPoolExecutor().getQueue() != null &&
                            this.getThreadPoolExecutor().getQueue() instanceof ResizeLinkedBlockingQueue) {
                        ((ResizeLinkedBlockingQueue) this.getThreadPoolExecutor().getQueue()).setCapacity(queueCapacity);
                    }
                    super.setQueueCapacity(queueCapacity);
                }

                @Override
                public void afterPropertiesSet() {
                    if (initialized) {
                        return;
                    }
                    super.afterPropertiesSet();
                    this.initialized = true;
                }
            };
            threadPoolExecutor.setCorePoolSize(corePoolSize);
            threadPoolExecutor.setMaxPoolSize(maxPoolSize);
            threadPoolExecutor.setQueueCapacity(queueCapacity);
            threadPoolExecutor.setKeepAliveSeconds(keepAliveSeconds);
            threadPoolExecutor.setThreadGroupName(name);
            threadPoolExecutor.setThreadNamePrefix(name + "-");
            if (threadFactory != null) {
                threadPoolExecutor.setThreadFactory(threadFactory);
            }
            if (rejectedExecutionHandler != null) {
                threadPoolExecutor.setRejectedExecutionHandler(rejectedExecutionHandler);
            }
            threadPoolExecutor.afterPropertiesSet();
            return threadPoolExecutor;
        });
    }

    /**
     * 获取所有线程池信息
     *
     * @return
     */
    public static List<ThreadPoolInfo> threadPoolInfoList() {
        return threadPoolMap
                .entrySet()
                .stream()
                .map(entry -> threadPoolInfo(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * 动态变更线程池（如：扩缩容、扩缩队列大小）
     *
     * @param threadPoolChange 变更线程池信息
     */
    public static void changeThreadPool(ThreadPoolChange threadPoolChange) {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = threadPoolMap.get(threadPoolChange.getName());
        if (threadPoolTaskExecutor == null) {
            throw new IllegalArgumentException();
        }
        if (threadPoolChange.getCorePoolSize() > threadPoolChange.getMaxPoolSize()) {
            throw new IllegalArgumentException();
        }
        threadPoolTaskExecutor.setCorePoolSize(threadPoolChange.getCorePoolSize());
        threadPoolTaskExecutor.setMaxPoolSize(threadPoolChange.getMaxPoolSize());
        threadPoolTaskExecutor.setQueueCapacity(threadPoolChange.getQueueCapacity());
    }


    /**
     * 获取所有线程池的信息
     *
     * @param name
     * @param threadPool
     * @return
     */
    private static ThreadPoolInfo threadPoolInfo(String name, ThreadPoolTaskExecutor threadPool) {
        ThreadPoolInfo threadPoolInfo = new ThreadPoolInfo();
        threadPoolInfo.setName(name);
        threadPoolInfo.setCorePoolSize(threadPool.getCorePoolSize());
        threadPoolInfo.setMaxPoolSize(threadPool.getMaxPoolSize());
        threadPoolInfo.setActiveCount(threadPool.getActiveCount());
        threadPoolInfo.setQueueCapacity(threadPool.getQueueCapacity());
        threadPoolInfo.setQueueSize(threadPool.getQueueSize());
        return threadPoolInfo;
    }

}
