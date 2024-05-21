package com.muzi.part11.job;

import com.muzi.part11.dto.Job;
import lombok.Data;

import java.util.List;

@Data
public class JobChange {
    //新增的job
    private List<Job> addJobList;
    //删除的job
    private List<Job> deleteJobList;
    //更新的job
    private List<Job> updateJobList;
}
