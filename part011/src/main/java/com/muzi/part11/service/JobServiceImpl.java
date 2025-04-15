package com.muzi.part11.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.muzi.part11.comm.ServiceExceptionUtils;
import com.muzi.part11.dto.Job;
import com.muzi.part11.dto.JobCreateRequest;
import com.muzi.part11.dto.JobUpdateRequest;
import com.muzi.part11.enums.JobStatusEnums;
import com.muzi.part11.mapper.JobMapper;
import com.muzi.part11.po.JobPO;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class JobServiceImpl extends ServiceImpl<JobMapper, JobPO> implements JobService {
    @Resource
    private JobMapper jobMapper;

    @Override
    public String jobCreate(JobCreateRequest request) {
        //1、参数校验
        this.valid(request);

        //2、入库
        JobPO jobPO = new JobPO();
        BeanUtils.copyProperties(request, jobPO);
        jobPO.setId(IdUtil.fastSimpleUUID());
        jobMapper.insert(jobPO);
        return jobPO.getId();
    }

    /**
     * 参数校验
     *
     * @param request
     */
    private void valid(JobCreateRequest request) {
        if (StringUtils.isBlank(request.getName())) {
            throw ServiceExceptionUtils.exception("job名称必填");
        }
        if (StringUtils.isBlank(request.getBeanName())) {
            throw ServiceExceptionUtils.exception("beanName名称不能为空");
        }
        if (StringUtils.isBlank(request.getBeanMethod())) {
            throw ServiceExceptionUtils.exception("beanMethod名称不能为空");
        }
        if (StringUtils.isBlank(request.getCron()) || !CronExpression.isValidExpression(request.getCron())) {
            throw ServiceExceptionUtils.exception("cron格式不正确");
        }
        if (!JobStatusEnums.isValid(request.getStatus())) {
            throw ServiceExceptionUtils.exception("status值无效");
        }
    }

    @Override
    public boolean jobUpdate(JobUpdateRequest request) {
        //1、参数校验
        if (StringUtils.isBlank(request.getId())) {
            throw ServiceExceptionUtils.exception("id不能为空");
        }
        this.valid(request);

        //2、更新
        JobPO jobPO = new JobPO();
        BeanUtils.copyProperties(request, jobPO);
        return this.updateById(jobPO);
    }

    @Override
    public boolean jobDelete(String id) {
        return this.removeById(id);
    }

    @Override
    public boolean jobStart(String id) {
        LambdaUpdateWrapper<JobPO> updateWrapper = Wrappers.lambdaUpdate(JobPO.class)
                .eq(JobPO::getId, id)
                .set(JobPO::getStatus, JobStatusEnums.START.getStatus());
        return this.update(updateWrapper);
    }

    @Override
    public boolean jobStop(String id) {
        LambdaUpdateWrapper<JobPO> updateWrapper = Wrappers.lambdaUpdate(JobPO.class)
                .eq(JobPO::getId, id)
                .set(JobPO::getStatus, JobStatusEnums.STOP.getStatus());
        return this.update(updateWrapper);
    }

    @Override
    public List<Job> jobList() {
        List<Job> list = this.list().stream().map(Job::of).collect(Collectors.toList());
        return list;
    }

    @Override
    public List<Job> getStartJobList() {
        LambdaQueryWrapper<JobPO> qw = Wrappers.lambdaQuery(JobPO.class).eq(JobPO::getStatus, JobStatusEnums.START.getStatus());
        return this.list(qw)
                .stream()
                .map(Job::of).collect(Collectors.toList());
    }
}
