package com.muzi.part1;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan(basePackages = "com.muzi.part1.mapper")
public class part1Application {

    public static void main(String[] args) {
        SpringApplication.run(part1Application.class, args);
    }

}
