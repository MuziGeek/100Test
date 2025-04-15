package com.muzi.part11.job;

import cn.hutool.json.JSONUtil;
import com.muzi.part11.dto.Job;
import com.muzi.part11.service.JobService;
import com.muzi.part11.utils.CollUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Component
public class SpringJobRunManager implements CommandLineRunner {
    private static Logger logger = LoggerFactory.getLogger(SpringJobRunManager.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    private JobService jobService;
    /**
     * 系统重正在运行中的job列表
     */
    private Map<String, SpringJobTask> runningJobMap = new ConcurrentHashMap<>();

    /**
     * springboot应用启动后会回调
     *
     * @param args incoming main method arguments
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        //1、启动job
        this.startAllJob();
        //2、监控db中job的变化（job增、删、改），然后同步给job执行器去执行
        this.monitorDbJobChange();
    }
    private void startAllJob() {
        List<Job> jobList = this.jobService.getStartJobList();
        for (Job job : jobList) {
            this.startJob(job);
        }

    }

    /**
     * 启动job
     *
     * @param job
     */
    private void startJob(Job job) {
        SpringJobTask springJobTask = new SpringJobTask(job, this.applicationContext);
        CronTrigger trigger = new CronTrigger(job.getCron());
        ScheduledFuture<?> scheduledFuture = this.threadPoolTaskScheduler.schedule(springJobTask, trigger);
        springJobTask.setScheduledFuture(scheduledFuture);
        runningJobMap.put(job.getId(), springJobTask);
        logger.info("启动 job 成功:{}", JSONUtil.toJsonStr(job));
    }

    /**
     * 监控db中job的变化，每5秒监控一次，这个频率大家使用的时候可以稍微调大点
     */
    private void monitorDbJobChange() {
        this.threadPoolTaskScheduler.scheduleWithFixedDelay(this::jobChangeDispose, Duration.ofSeconds(5));
    }

    private void jobChangeDispose() {
        try {
            //1、从db中拿到所有job，和目前内存中正在运行的所有job对比，可得到本次新增的job、删除的job、更新的job
            JobChange jobChange = this.getJobChange();
            //2、处理新增的job
            for (Job job : jobChange.getAddJobList()) {
                this.startJob(job);
            }
            //3、处理删除的job
            for (Job job : jobChange.getDeleteJobList()) {
                this.deleteJob(job);
            }
            //4、处理变化的job
            for (Job job : jobChange.getUpdateJobList()) {
                this.updateJob(job);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private JobChange getJobChange() {
        //新增的job
        List<Job> addJobList = new ArrayList<>();
        //删除的job
        List<Job> deleteJobList = new ArrayList<>();
        //更新的job
        List<Job> updateJobList = new ArrayList<>();

        //从db中拿到所有job，和目前内存中正在运行的所有job对比，可得到本次新增的job、删除的job、更新的job
        List<Job> startJobList = this.jobService.getStartJobList();
        for (Job job : startJobList) {
            SpringJobTask springJobTask = runningJobMap.get(job.getId());
            if (springJobTask == null) {
                addJobList.add(job);
            } else {
                //job的执行规则变了
                if (jobIsChange(job, springJobTask.getJob())) {
                    updateJobList.add(job);
                }
            }
        }

        //获取被删除的job，springJobTaskMap中存在的，而startJobList不存在的，则是需要从当前运行列表中停止移除的
        Set<String> startJobIdList = CollUtils.convertSet(startJobList, Job::getId);
        for (Map.Entry<String, SpringJobTask> springJobTaskEntry : runningJobMap.entrySet()) {
            if (!startJobIdList.contains(springJobTaskEntry.getKey())) {
                deleteJobList.add(springJobTaskEntry.getValue().getJob());
            }
        }

        //返回job变更结果
        JobChange jobChange = new JobChange();
        jobChange.setAddJobList(addJobList);
        jobChange.setUpdateJobList(updateJobList);
        jobChange.setDeleteJobList(deleteJobList);
        return jobChange;
    }

    /**
     * 检测两个job是否发生了变化，（cron、beanName、beanMethod）中有任意一项变动了，则返回true
     *
     * @param job1
     * @param job2
     * @return
     */
    private boolean jobIsChange(Job job1, Job job2) {
        return !(Objects.equals(job1.getCron(), job2.getCron()) &&
                Objects.equals(job1.getBeanName(), job2.getBeanName()) &&
                Objects.equals(job1.getBeanMethod(), job2.getBeanMethod()));
    }

    /**
     * 删除job
     *
     * @param job
     */
    private void deleteJob(Job job) {
        if (job == null) {
            return;
        }
        SpringJobTask springJobTask = this.runningJobMap.get(job.getId());
        if (springJobTask == null) {
            return;
        }
        //取消job的执行
        springJobTask.getScheduledFuture().cancel(false);
        //将job从map中移除
        runningJobMap.remove(job.getId());
        logger.info("移除 job 成功:{}", JSONUtil.toJsonStr(job));
    }

    public void updateJob(Job job) {
        //1、先删除旧的job
        this.deleteJob(job);
        //2、运行新的job
        this.startJob(job);
        logger.info("更新 job 成功:{}", JSONUtil.toJsonStr(job));
    }

}
