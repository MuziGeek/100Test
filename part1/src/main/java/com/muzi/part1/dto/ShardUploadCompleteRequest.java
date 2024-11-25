package com.muzi.part1.dto;


import lombok.Data;


@Data
public class ShardUploadCompleteRequest {
    /**
     * 分片上传任务id（由初始化分片接口返回的）
     */
    private String shardUploadId;
}
