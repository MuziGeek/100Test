package com.muzi.part1;

import cn.hutool.core.lang.Console;
import cn.hutool.crypto.SecureUtil;
import com.muzi.part1.comm.Result;
import com.muzi.part1.dto.ShardUploadCompleteRequest;
import com.muzi.part1.dto.ShardUploadDetailResponse;
import com.muzi.part1.dto.ShardUploadInitRequest;
import com.muzi.part1.utils.ShardUploadUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class ShardUploadTest {
    RestTemplate restTemplate = new RestTemplate();
    //需要上传的文件
    File file = new File("D:\\CPIC-Feishu.zip");

    //定义每个分片文件大小（20MB，可以自定义）
    long partSize = 20 * 1024 * 1024;

    @Test
    public void shardUpload() throws Exception {
        long begin = System.currentTimeMillis();
        int partNum = ShardUploadUtils.shardNum(file.length(), partSize);
        String fileMd5 = SecureUtil.md5(file);
        log.info("分片上传，初始化，文件:{}", file.getAbsoluteFile());
        log.info("分片数量:{},分片文件大小：{},文件md5:{}", partNum, partNum, fileMd5);

        //1、分片上传初始化
        String shardUploadId = this.shardUploadInit(file.getName(), partNum, fileMd5);
        log.info("分片上传，初始化完毕，shardUploadId:{}", shardUploadId);

       /* //2、循环上传分片
        for (int partOrder = 1; partOrder <= partNum; partOrder++) {
            this.shardUploadPart(shardUploadId, partOrder);
        }*/
        //2 多线程上传分片
        /*for (int i = 1; i <partNum ; i++) {
            MyRunnable myRunnable = new MyRunnable();
            myRunnable.setPartOrder(i);
            myRunnable.setShardUploadId(shardUploadId);
            Thread thread = new Thread(myRunnable);
            thread.start();
        }*/
        ExecutorService executorService = Executors.newFixedThreadPool(partNum);
        CountDownLatch countDownLatch = new CountDownLatch(partNum);
        for (int i = 1; i <=partNum ; i++) {
            int partorder =i;
            executorService.execute(() -> {
                try {
                    ShardUploadTest shardUploadTest = new ShardUploadTest();
                    shardUploadTest.shardUploadPart(shardUploadId,partorder);

                } catch (Exception e){
                    log.info("分片上传失败{}", e);

                }finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        executorService.shutdown();

        //3、合并分片，完成上传
        this.shardUploadComplete(shardUploadId);

        //4、获取分片任务的详细信息(分片是否上传完成、哪些分片文件是否已上传)
        ShardUploadDetailResponse detail = this.shardUploadDetail(shardUploadId);
        long end = System.currentTimeMillis();
        log.info("运行时间：{}",end-begin);
        log.info("分片任务详细信息:{}", detail);
    }

    public ShardUploadDetailResponse shardUploadDetail(String shardUploadId) {
        RequestEntity<Void> entity = RequestEntity
                .get(this.getRequestUrl("shardUpload/detail?shardUploadId=" + shardUploadId))
                .build();
        ResponseEntity<Result<ShardUploadDetailResponse>> exchange = this.restTemplate.exchange(entity, new ParameterizedTypeReference<Result<ShardUploadDetailResponse>>() {
        });
        return exchange.getBody().getData();
    }

    public void shardUploadPart(String shardUploadId, int partOrder) throws Exception {
        byte[] bytes = readPart(partOrder);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("shardUploadId", shardUploadId);
        body.add("partOrder", partOrder);
        body.add("file", new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
                return "part" + partOrder;
            }
        });
        RequestEntity<MultiValueMap<String, Object>> entity = RequestEntity
                .post(this.getRequestUrl("shardUpload/uploadPart"))
                .body(body);
        this.restTemplate.exchange(entity, new ParameterizedTypeReference<Result<String>>() {
        });
        log.info("第{}个分片上传完毕", partOrder);
    }

    public String shardUploadInit(String fileName, int partNum, String md5) {
        ShardUploadInitRequest request = new ShardUploadInitRequest();
        request.setFileName(fileName);
        request.setPartNum(partNum);
        request.setMd5(md5);

        RequestEntity<ShardUploadInitRequest> entity = RequestEntity
                .post(this.getRequestUrl("shardUpload/init"))
                .contentType(MediaType.APPLICATION_JSON)
                .body(request);
        ResponseEntity<Result<String>> exchange = this.restTemplate.exchange(entity, new ParameterizedTypeReference<Result<String>>() {
        });
        return exchange.getBody().getData();
    }

    public void shardUploadComplete(String shardUploadId) {
        ShardUploadCompleteRequest request = new ShardUploadCompleteRequest();
        request.setShardUploadId(shardUploadId);

        RequestEntity<ShardUploadCompleteRequest> entity = RequestEntity
                .post(this.getRequestUrl("shardUpload/complete"))
                .contentType(MediaType.APPLICATION_JSON)
                .body(request);
        ResponseEntity<Result<Boolean>> responseEntity = this.restTemplate.exchange(entity, new ParameterizedTypeReference<Result<Boolean>>() {
        });
        log.info("文件上传完成：{}", responseEntity.getBody());
    }


    public byte[] readPart(int partOrder) throws Exception {
        RandomAccessFile randomAccessFile = null;
        byte[] bytes = new byte[(int) partSize];
        try {
            // 创建一个随机访问文件流，以读写模式
            randomAccessFile = new RandomAccessFile(file, "r");
            // 跳到文件当前分片的位置开始读取一个分片的内容
            randomAccessFile.seek((partOrder - 1) * partSize);
            int read = randomAccessFile.read(bytes);
            if (read == partSize) {
                return bytes;
            } else {
                byte[] tempBytes = new byte[read];
                System.arraycopy(bytes, 0, tempBytes, 0, read);
                return tempBytes;
            }

        } finally {
            IOUtils.closeQuietly(randomAccessFile);
        }
    }

   /* class MyRunnable implements Runnable {

        private int partOrder;

        public void setPartOrder(int partOrder) {
            this.partOrder = partOrder;
        }
        private String shardUploadId;

        public void setShardUploadId(String shardUploadId) {
            this.shardUploadId = shardUploadId;
        }

        public void run() {
                try {
                    ShardUploadTest shardUploadTest = new ShardUploadTest();
                    shardUploadTest.shardUploadPart(shardUploadId,partOrder);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }*/

    public String getRequestUrl(String path) {
        return String.format("http://localhost:8080/%s", path);
    }

    public static void main(String[] args) {


        //2 多线程上传分片
        for (int i = 1; i <10 ; i++) {
            Thread thread = new Thread(()->{
                Console.log(Thread.currentThread().getName());},"t"+i);
            thread.start();
        }



    }


}

