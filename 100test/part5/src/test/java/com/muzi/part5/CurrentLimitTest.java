package com.muzi.part5;

import org.springframework.web.client.RestTemplate;

import java.util.concurrent.atomic.AtomicInteger;

public class CurrentLimitTest {
    public static void main(String[] args) throws InterruptedException {
        // 记录成功量、失败量
        AtomicInteger successNum = new AtomicInteger(0);
        AtomicInteger failNum = new AtomicInteger(0);

        //下面模拟200个人同时下单，运行，大家看结果
        RestTemplate restTemplate = new RestTemplate();
        Runnable requestPlaceOrder = () -> {
            String result = restTemplate.getForObject("http://localhost:8080/counter", String.class);
            System.out.println(result);
            if ("下单成功".equals(result)) {
                successNum.incrementAndGet();
            } else {
                failNum.incrementAndGet();
            }
        };

        //模拟100个人同时发送100个请求，待请求结束，看成功量、失败量
        LoadRunnerUtils.LoadRunnerResult loadRunnerResult = LoadRunnerUtils.run(100, 100, requestPlaceOrder);
        System.out.println(loadRunnerResult);

        System.out.println("下单成功数：" + successNum.get());
        System.out.println("下单失败数：" + failNum.get());
    }

}
