package com.muzi.part5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * 使用 Semaphore 实现最简单版本的限流功能
 */
@ServletComponentScan
@SpringBootApplication(scanBasePackages = {"com.muzi.part5"})
public class part5Application {

    public static void main(String[] args) {
        SpringApplication.run(com.muzi.part5.part5Application.class, args);
    }

}
