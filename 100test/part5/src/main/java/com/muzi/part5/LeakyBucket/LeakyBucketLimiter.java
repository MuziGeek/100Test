package com.muzi.part5.LeakyBucket;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


public class LeakyBucketLimiter {
    /**
     * 桶的容量
     */
    private final int capacity;
    /**
     * 漏桶的漏出速率，单位时间内漏出水的数量
     */
    private final int leakRate;
    /**
     * 当前桶中的水量
     */
    private volatile int water = 0;
    /**
     * 上次漏水的时间
     */
    private volatile long lastLeakTime = System.currentTimeMillis();

    /**
     * 漏桶容器
     */
    private static final ConcurrentHashMap<String, LeakyBucketLimiter> LIMITER_MAP = new ConcurrentHashMap<>();

    /**
     * 静态工厂方法，确保相同的方法使用相同的漏桶实例
     *
     * @param methodKey 方法名
     * @param capacity
     * @param leakRate
     * @return
     */
    public static LeakyBucketLimiter createLimiter(String methodKey, int capacity, int leakRate) {
        return LIMITER_MAP.computeIfAbsent(methodKey, k -> new LeakyBucketLimiter(capacity, leakRate));
    }

    private LeakyBucketLimiter(int capacity, int leakRate) {
        this.capacity = capacity;
        this.leakRate = leakRate;
    }

    /**
     * 尝试获取许可（try to acquire a permit），如果获取成功返回true，否则返回false
     *
     * @return
     */
    public boolean tryAcquire() {
        long currentTime = System.currentTimeMillis();
        synchronized (this) {
            // 计算上次漏水到当前时间的时间间隔
            long leakDuration = currentTime - lastLeakTime;
            // 如果时间间隔大于等于1秒，表示漏桶已经漏出一定数量的水
            if (leakDuration >= TimeUnit.SECONDS.toMillis(1)) {
                // 计算漏出的水量
                long leakQuantity = leakDuration / TimeUnit.SECONDS.toMillis(1) * leakRate;
                // 漏桶漏出水后，更新桶中的水量，但不能低于0
                water = (int) Math.max(0, water - leakQuantity);
                lastLeakTime = currentTime;
            }
            // 判断桶中的水量是否小于容量，如果是则可以继续添加水（相当于获取到令牌）
            if (water < capacity) {
                water++;
                return true;
            }
        }
        // 如果桶满，则获取令牌失败
        return false;
    }
}