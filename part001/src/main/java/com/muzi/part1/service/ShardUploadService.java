package com.muzi.part1.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.muzi.part1.dto.ShardUploadCompleteRequest;
import com.muzi.part1.dto.ShardUploadDetailResponse;
import com.muzi.part1.dto.ShardUploadInitRequest;
import com.muzi.part1.dto.ShardUploadPartRequest;
import com.muzi.part1.po.ShardUploadPO;

import java.io.IOException;

public interface ShardUploadService extends IService<ShardUploadPO> {
    /**
     * 创建分片上传任务
     *
     * @param request
     * @return 分片任务id
     */
    String init(ShardUploadInitRequest request);

    /**
     * 上传分片
     *
     * @param request
     */
    void uploadPart(ShardUploadPartRequest request) throws IOException;

    /**
     * 完成分片上传，合并分片文件
     *
     * @param request
     */
    void complete(ShardUploadCompleteRequest request) throws IOException;

    /**
     * 获取分片任务详细信息
     *
     * @param shardUploadId 分片任务id
     * @return
     */
    ShardUploadDetailResponse detail(String shardUploadId);
}
