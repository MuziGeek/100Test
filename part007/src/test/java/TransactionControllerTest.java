import com.muzi.part3.utils.LoadRunnerUtils;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.atomic.AtomicInteger;


public class TransactionControllerTest {

    @Test
    public void test() throws InterruptedException {
        System.out.println("对这两种事务的接口进行测试，分别对他们进行模拟200个并发请求，然后输出成功数量和失败的数量");
        //对声明式事务的接口进行测试，这个接口内部是大事务
        System.out.println("--------------声明式事务接口压测结果------------------");
        test("http://localhost:8080/bigTransaction");

        //对编程式事务的接口进行测试，这个接口内部是小事务
        System.out.println("--------------编程式事务接口压测结果------------------");
        test("http://localhost:8080/smallTransaction");
    }

    private static void test(String url) throws InterruptedException {
        //对 url 指定的接口模拟200个并发请求，然后输出成功数量和失败的数量
        AtomicInteger successNum = new AtomicInteger();
        AtomicInteger failNum = new AtomicInteger();
        RestTemplate restTemplate = new RestTemplate();

        // 这里我们使用上节课写的压测工具类，模拟200个并发进行测试
        LoadRunnerUtils.run(200, 200, () -> {
            Boolean result = restTemplate.getForObject(url, Boolean.class);
            if (result) {
                successNum.incrementAndGet();
            } else {
                failNum.incrementAndGet();
            }
        });
        System.out.println("请求成功数:" + successNum.get());
        System.out.println("请求失败数:" + failNum.get());
    }
}
