package com.muzi.part17.controller;



import com.muzi.part17.common.Result;
import com.muzi.part17.common.ResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;


@RestController
public class HelloController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping
    public Result<String> hello() throws InterruptedException {
        logger.info("开始执行业务");
        TimeUnit.MILLISECONDS.sleep(500);
        logger.info("业务执行结束");
        return ResultUtils.success("111111");
    }

    @GetMapping("/exception")
    public Result<String> exception() throws InterruptedException {
        logger.info("开始执行业务");
        //这里模拟了一个错误，10/0，会报错
        System.out.println(10 / 0);
        logger.info("业务执行结束");
        return ResultUtils.success("22222");
    }
}
