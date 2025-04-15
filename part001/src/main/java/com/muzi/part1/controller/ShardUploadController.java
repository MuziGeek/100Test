package com.muzi.part1.controller;

import com.muzi.part1.comm.Result;
import com.muzi.part1.comm.ResultUtils;
import com.muzi.part1.dto.ShardUploadCompleteRequest;
import com.muzi.part1.dto.ShardUploadDetailResponse;
import com.muzi.part1.dto.ShardUploadInitRequest;
import com.muzi.part1.dto.ShardUploadPartRequest;
import com.muzi.part1.service.ShardUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/shardUpload")
public class ShardUploadController {
    @Autowired
    private ShardUploadService shardUploadService;

    /**
     * 创建分片上传任务
     *
     * @return 分片任务id
     */
    @PostMapping("/init")
    public Result<String> init(@RequestBody ShardUploadInitRequest request) {
        String shardUploadId = this.shardUploadService.init(request);
        return ResultUtils.ok(shardUploadId);
    }

    /**
     * 上传分片（客户端需遍历上传分片文件）
     *
     * @return
     */
    @PostMapping("/uploadPart")
    public Result<Boolean> uploadPart(ShardUploadPartRequest request) throws IOException {
        this.shardUploadService.uploadPart(request);
        return ResultUtils.ok(true);
    }

    /**
     * 合并分片，完成上传
     *
     * @return
     */
    @PostMapping("/complete")
    public Result<Boolean> complete(@RequestBody ShardUploadCompleteRequest request) throws IOException {
        this.shardUploadService.complete(request);
        return ResultUtils.ok(true);
    }

    /**
     * 获取分片任务详细信息
     *
     * @param shardUploadId 分片任务id
     * @return
     */
    @GetMapping("/detail")
    public Result<ShardUploadDetailResponse> detail(@RequestParam("shardUploadId") String shardUploadId) {
        return ResultUtils.ok(this.shardUploadService.detail(shardUploadId));
    }
}
