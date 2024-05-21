package com.muzi.part11.dto;

import com.itsoku.lesson011.po.JobPO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * <b>description</b>： Java高并发、微服务、性能优化实战案例100讲，视频号：程序员路人，源码 & 文档 & 技术支持，请加个人微信号：itsoku <br>
 * <b>time</b>：2024/4/3 0:13 <br>
 * <b>author</b>：ready likun_557@163.com
 */
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
