package com.muzi.part11.controller;


import com.muzi.part11.comm.Result;
import com.muzi.part11.comm.ResultUtils;
import com.muzi.part11.dto.Job;
import com.muzi.part11.dto.JobCreateRequest;
import com.muzi.part11.dto.JobUpdateRequest;
import com.muzi.part11.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class JobController {
    @Autowired
    private JobService jobService;

    /**
     * 创建job
     *
     * @param request
     * @return 返回job的id
     */
    @PostMapping("/jobCreate")
    public Result<String> jobCreate(@RequestBody JobCreateRequest request) {
        return ResultUtils.ok(this.jobService.jobCreate(request));
    }

    /**
     * 更新job
     *
     * @param request
     */
    @PostMapping("/jobUpdate")
    public Result<Boolean> jobUpdate(@RequestBody JobUpdateRequest request) {
        return ResultUtils.ok(this.jobService.jobUpdate(request));
    }

    /**
     * 删除job
     *
     * @param id
     */
    @PostMapping("/jobDelete")
    public Result<Boolean> jobDelete(@RequestParam("id") String id) {
        return ResultUtils.ok(this.jobService.jobDelete(id));
    }

    /**
     * 启用job
     *
     * @param id
     */
    @PostMapping("/jobStart")
    public Result<Boolean> jobStart(@RequestParam("id") String id) {
        return ResultUtils.ok(this.jobService.jobStart(id));
    }

    /**
     * 停止job
     *
     * @param id
     */
    @PostMapping("/jobStop")
    public Result<Boolean> jobStop(@RequestParam("id") String id) {
        return ResultUtils.ok(this.jobService.jobStop(id));
    }

    /**
     * 所有的job
     *
     * @return
     */
    @GetMapping
    public Result<List<Job>> jobList() {
        return ResultUtils.ok(this.jobService.jobList());
    }

}
