package com.muzi.part4.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.muzi.part4.po.GoodsPO;
import org.apache.ibatis.annotations.Param;


 public interface GoodsMapper extends BaseMapper<GoodsPO> {
    /**
     * 下单
     *
     * @param goodsId 商品id
     * @param num     商品数量
     * @return
     */
    int placeOrder1(@Param("goodsId") String goodsId, @Param("num") int num);

    /**
     * 下单
     *
     * @param goodsId       商品id
     * @param num           商品数量
     * @param expectVersion version 期望值
     * @return
     */
    int placeOrder2(@Param("goodsId") String goodsId, @Param("num") int num, @Param("expectVersion") long expectVersion);

    /**
     * 下单
     *
     * @param goodsId 商品id
     * @param num     商品数量
     * @return
     */
    int placeOrder3(@Param("goodsId") String goodsId, @Param("num") int num);

}
