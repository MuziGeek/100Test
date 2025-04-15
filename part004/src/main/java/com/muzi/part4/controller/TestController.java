package com.muzi.part4.controller;


import com.muzi.part4.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestController {
    @Autowired
    private GoodsService goodsService;

    @GetMapping("/test1")
    public String test1() throws InterruptedException {
        this.goodsService.placeOrder1();
        return "ok";
    }

    @GetMapping("/test2")
    public String test2() throws InterruptedException {
        this.goodsService.placeOrder2();
        return "ok";
    }

    @GetMapping("/test3")
    public String test3() throws InterruptedException {
        this.goodsService.placeOrder3();
        return "ok";
    }

    @GetMapping("/test4")
    public String test4() throws InterruptedException {
        this.goodsService.placeOrder4();
        return "ok";
    }
}
