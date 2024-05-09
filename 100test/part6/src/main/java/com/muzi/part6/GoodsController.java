package com.itsoku.lesson006;

import com.muzi.part6.GoodsDetailResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


@RestController
@Slf4j
public class GoodsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    private ThreadPoolTaskExecutor goodsThreadPool;

    /**
     * 根据商品id获取商品信息(基本信息、描述信息、评论量，收藏量)
     *
     * @param goodsId 商品id
     * @return
     * @throws InterruptedException
     */
    @GetMapping("/getGoodsDetail")
    public GoodsDetailResponse getGoodsDetail(@RequestParam("goodsId") String goodsId) {
        long st = System.currentTimeMillis();
        GoodsDetailResponse goodsDetailResponse = new GoodsDetailResponse();
        // 1、获取商品基本信息，耗时100ms
        goodsDetailResponse.setGoodsInfo(this.getGoodsInfo(goodsId));

        //2、获取商品描述信息，耗时100ms
        goodsDetailResponse.setGoodsDescription(this.getGoodsDescription(goodsId));

        //3、获取商品评论量，耗时100ms
        goodsDetailResponse.setCommentCount(this.getGoodsCommentCount(goodsId));

        //4、获取商品收藏量，耗时100ms
        goodsDetailResponse.setFavoriteCount(this.getGoodsFavoriteCount(goodsId));

        LOGGER.info("获取商品信息，普通版耗时：{} ms", (System.currentTimeMillis() - st));
        return goodsDetailResponse;
    }

    /**
     * 优化后的方法，根据商品id获取商品信息(基本信息、描述信息、评论量，收藏量)
     *
     * @param goodsId 商品id
     * @return
     * @throws InterruptedException
     */
    @GetMapping("/getGoodsDetailNew")
    public GoodsDetailResponse getGoodsDetailNew(@RequestParam("goodsId") String goodsId) {
        long st = System.currentTimeMillis();
        GoodsDetailResponse goodsDetailResponse = new GoodsDetailResponse();

        // 1、获取商品基本信息，耗时100ms
        CompletableFuture<Void> goodsInfoCf = CompletableFuture.runAsync(() -> goodsDetailResponse.setGoodsInfo(this.getGoodsInfo(goodsId)), this.goodsThreadPool);

        //2、获取商品描述信息，耗时100ms
        CompletableFuture<Void> goodsDescriptionCf = CompletableFuture.runAsync(() -> goodsDetailResponse.setGoodsDescription(this.getGoodsDescription(goodsId)), this.goodsThreadPool);

        //3、获取商品评论量，耗时100ms
        CompletableFuture<Void> goodsCommentCountCf = CompletableFuture.runAsync(() -> goodsDetailResponse.setCommentCount(this.getGoodsCommentCount(goodsId)), this.goodsThreadPool);

        //4、获取商品收藏量，耗时100ms
        CompletableFuture<Void> goodsFavoriteCountCf = CompletableFuture.runAsync(() -> goodsDetailResponse.setFavoriteCount(this.getGoodsFavoriteCount(goodsId)), this.goodsThreadPool);

        //等待上面执行结束
        CompletableFuture.allOf(goodsInfoCf, goodsDescriptionCf, goodsCommentCountCf, goodsFavoriteCountCf).join();

        LOGGER.info("获取商品信息，使用线程池并行查询耗时：{} ms", (System.currentTimeMillis() - st));

        return goodsDetailResponse;
    }

    private int getGoodsFavoriteCount(String goodsId) {
        try {
            log.info("获取商品收藏量");
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        return 10000;
    }

    private int getGoodsCommentCount(String goodsId) {
        try {
            log.info("获取商品评论量");
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        return 10000;
    }

    public String getGoodsDescription(String goodsId) {
        try {
            log.info("获取商品描述");
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        return "商品描述信息";
    }


    public String getGoodsInfo(String goodsId) {
        try {
            log.info("获取商品基本信息");
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        return "商品基本信息";
    }

}
