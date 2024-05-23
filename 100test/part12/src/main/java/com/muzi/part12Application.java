package com.muzi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan(basePackages = "com.muzi.mapper")
public class part12Application {

    public static void main(String[] args) {
        SpringApplication.run(part12Application.class, args);
    }

}

