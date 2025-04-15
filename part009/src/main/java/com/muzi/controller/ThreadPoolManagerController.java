package com.muzi.controller;



import com.muzi.comm.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/threadPoolManager")
public class ThreadPoolManagerController {
    /**
     * 获取所有的线程池信息
     *
     * @return
     */
    @GetMapping("/threadPoolInfoList")
    public Result<List<ThreadPoolInfo>> threadPoolInfoList() {
        return ResultUtils.ok(ThreadPoolManager.threadPoolInfoList());
    }

    /**
     * 线程池扩缩容
     *
     * @return
     */
    @PostMapping("/threadPoolChange")
    public Result<Boolean> threadPoolChange(@RequestBody ThreadPoolChange threadPoolChange) {
        ThreadPoolManager.changeThreadPool(threadPoolChange);
        return ResultUtils.ok(true);
    }
}
