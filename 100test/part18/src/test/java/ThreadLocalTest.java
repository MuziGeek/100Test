import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadLocalTest {
    Logger logger = LoggerFactory.getLogger(ThreadLocalTest.class);
    //①：这里创建了一个 ThreadLocal
    ThreadLocal<String> userNameTL = new ThreadLocal<>();

    /**
     * ThreadLocal 可以在当前线程中存储数据
     *
     * @throws InterruptedException
     */
    @Test
    public void threadLocalTest1() throws InterruptedException {
        //将用户名放入 userNameTL 中
        userNameTL.set("张三");

        //在m1中，取上面放入的张三，看看能不能取到
        m1();

        //这里创建了线程 thread1，里面放入了李四，然后在m1中取出用户名，看看是不是李四?
        new Thread(() -> {
            userNameTL.set("李四");
            m1();
        }, "thread1").start();
        TimeUnit.SECONDS.sleep(1);

        //这里创建了线程 thread2，里面放入了王五，然后在m1中取出用户名，看看是不是王五
        new Thread(() -> {
            userNameTL.set("王五");
            m1();
        }, "thread2").start();
        TimeUnit.SECONDS.sleep(1);
    }


    public void m1() {
        logger.info("userName:{}", userNameTL.get());
    }

    /**
     * 子线程能否获取父线程ThreadLocal中的值呢？
     *
     * @throws InterruptedException
     */
    @Test
    public void threadLocalTest2() throws InterruptedException {
        //这里是主线程，ThreadLocal中设置了值：张三
        userNameTL.set("张三");
        logger.info("userName:{}", userNameTL.get());

        //创建了一个子线程thread1，在子线程中去ThreadLocal中拿值，能否拿到刚才放进去的“张三”呢？
        new Thread(() -> {
            logger.info("userName:{}", userNameTL.get());
        }, "thread1").start();

        TimeUnit.SECONDS.sleep(1);
    }


    // 这里定义了一个 InheritableThreadLocal 对象
    private InheritableThreadLocal<String> userNameItl = new InheritableThreadLocal<>();

    @Test
    public void inheritableThreadLocal1() throws InterruptedException {
        //这里是主线程，使用 InheritableThreadLocal.set 放入值：张三
        userNameItl.set("张三");
        logger.info("userName:{}", userNameItl.get());

        //创建了一个子线程thread1，在子线程中去ThreadLocal中拿值，能否拿到刚才放进去的“张三”呢？
        new Thread(() -> {
            logger.info("userName:{}", userNameItl.get());
        }, "thread1").start();
        TimeUnit.SECONDS.sleep(1);
    }

    /**
     * InheritableThreadLocal：遇到线程池，会有问题
     *
     * @throws InterruptedException
     */
    @Test
    public void inheritableThreadLocal2() throws InterruptedException {
        //为了看到效果，这里创建大小为1的线程池，注意这里为1才能方便看到效果
        ExecutorService executorService = Executors.newFixedThreadPool(1);

        //主线程中，放入了张三
        userNameItl.set("张三");
        logger.info("userName:{}", userNameItl.get());

        //在线程池中通过 InheritableThreadLocal 拿值，看看能否拿到 刚才放入的张三？
        executorService.execute(() -> {
            logger.info("第1次获取 userName:{}", userNameItl.get());
        });

        //这里稍微休眠一下，等待上面的任务结束
        TimeUnit.SECONDS.sleep(1);

        //这里又在主线程中放入了李四
        userNameItl.set("李四");
        logger.info("userName:{}", userNameItl.get());

        //这里又在线程池中通过 InheritableThreadLocal.get 方法拿值，看看能否拿到 刚才放入的李四？
        executorService.execute(() -> {
            //在线程池中通过 inheritableThreadLocal 拿值，看看能否拿到？
            logger.info("第2次获取 userName:{}", userNameItl.get());
        });

        TimeUnit.SECONDS.sleep(1);
    }
    TransmittableThreadLocal<String> userNameTtl = new TransmittableThreadLocal<String>();

    @Test
    public void transmittableThreadLocal1() throws InterruptedException {
        //为了看到效果，这里创建大小为1的线程池，注意这里为1才能方便看到效果
        ExecutorService executorService = Executors.newFixedThreadPool(1);

        //这里需要用 TtlExecutors.getTtlExecutorService 将原线程池包装下
        executorService = TtlExecutors.getTtlExecutorService(executorService);

        // 主线程中设置 张三
        userNameTtl.set("张三");
        logger.info("userName:{}", userNameTtl.get());

        //在线程池中通过 TransmittableThreadLocal 拿值，看看能否拿到 刚才放入的张三？
        executorService.execute(() -> {
            logger.info("第1次获取 userName:{}", userNameTtl.get());
        });
        TimeUnit.SECONDS.sleep(1);

        //这里放入了李四
        userNameTtl.set("李四");
        logger.info("userName:{}", userNameTtl.get());

        //在线程池中通过 TransmittableThreadLocal 拿值，看看能否拿到 刚才放入的李四？
        executorService.execute(() -> {
            //在线程池中通过 inheritableThreadLocal 拿值，看看能否拿到？
            logger.info("第2次获取 userName:{}", userNameTtl.get());
        });

        TimeUnit.SECONDS.sleep(1);
    }
}
