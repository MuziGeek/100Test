package com.muzi.part7.controller;

import com.muzi.part7.service.part7Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class part7Controller {
    private static final Logger LOGGER = LoggerFactory.getLogger(part7Controller.class);
    @Autowired
    private part7Service part7Service;

    /**
     * 声明式事务，事务范围比较大
     *
     * @throws InterruptedException
     */
    @GetMapping("/bigTransaction")
    public boolean bigTransaction() {
        try {
            this.part7Service.bigTransaction();
            return true;
        } catch (Exception e) {
            LOGGER.error("声明式事务 执行异常:{}", e.getMessage());
            return false;
        }
    }

    /**
     * 使用 TransactionTemplate 编程式事务，可以灵活的控制事务的范围
     *
     * @throws InterruptedException
     */
    @GetMapping("/smallTransaction")
    public boolean smallTransaction() {
        try {
            this.part7Service.smallTransaction();
            return true;
        } catch (Exception e) {
            LOGGER.error("编程式事务 执行异常:{}", e.getMessage());
            return false;
        }
    }
}
