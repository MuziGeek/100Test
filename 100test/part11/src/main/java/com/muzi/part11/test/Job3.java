package com.muzi.part11.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class Job3 {
    public void execute() {
        log.info("job3");
    }
}
