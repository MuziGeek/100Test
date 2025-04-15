package com.muzi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan(basePackages = "com.itsoku.lesson004.mapper")
public class part4Application {

    public static void main(String[] args) {
        SpringApplication.run(com.muzi.part4Application.class, args);
    }

}
