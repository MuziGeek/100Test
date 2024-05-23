package com.muzi.part11.dto;

import com.muzi.part11.po.JobPO;
import lombok.Data;
import org.springframework.beans.BeanUtils;


@Data
public class Job {
    //job的id
    private String id;
    //job的名称
    private String name;
    //job的执行周期，cron表达式
    private String cron;
    //job需要执行那个bean，对应spring中bean的名称
    private String beanName;
    //job执行的bean的方法
    private String beanMethod;

    public static Job of(JobPO jobPO) {
        if (jobPO == null) {
            return null;
        }
        Job job = new Job();
        BeanUtils.copyProperties(jobPO, job);
        return job;
    }
}
