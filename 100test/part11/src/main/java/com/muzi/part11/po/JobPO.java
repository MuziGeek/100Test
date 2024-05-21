package com.muzi.part11.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * <b>description</b>： Java高并发、微服务、性能优化实战案例100讲，视频号：程序员路人，源码 & 文档 & 技术支持，请加个人微信号：itsoku <br>
 * <b>time</b>：2024/4/3 0:13 <br>
 * <b>author</b>：ready likun_557@163.com
 */
@TableName("t_job")
@Data
public class JobPO {
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
    //job的状态,0：停止，1：执行中
    private Integer status;
}
