package com.muzi.part11.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


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
