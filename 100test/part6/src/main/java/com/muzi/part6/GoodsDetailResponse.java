package com.muzi.part6;

import lombok.Data;

@Data
public class GoodsDetailResponse {
    //商品基本信息
    private String goodsInfo;

    // 商品描述信息
    private String goodsDescription;

    // 商品评论量
    private int commentCount;

    // 收藏量
    private int favoriteCount;
}
