package com.muzi.part11.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.muzi.part11.dto.Job;
import com.muzi.part11.dto.JobCreateRequest;
import com.muzi.part11.dto.JobUpdateRequest;
import com.muzi.part11.po.JobPO;

import java.util.List;


public interface JobService extends IService<JobPO> {
    /**
     * 创建job
     *
     * @param request
     * @return 返回job的id
     */
    String jobCreate(JobCreateRequest request);

    /**
     * 更新job
     *
     * @param request
     */
    boolean jobUpdate(JobUpdateRequest request);

    /**
     * 删除job
     *
     * @param id
     */
    boolean jobDelete(String id);

    /**
     * 启用job
     *
     * @param id
     */
    boolean jobStart(String id);

    /**
     * 停止job
     *
     * @param id
     */
    boolean jobStop(String id);

    /**
     * 所有的job
     *
     * @return
     */
    List<Job> jobList();

    /**
     * 获取需要启动的job列表
     *
     * @return
     */
    List<Job> getStartJobList();
}
