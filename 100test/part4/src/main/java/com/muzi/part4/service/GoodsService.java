package com.muzi.part4.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.muzi.part4.po.GoodsPO;

public interface GoodsService extends IService<GoodsPO> {

    /**
     * 1、第一种解决超卖的方法：update t_goods set num = num - #{购买的商品数量} where goods_id = #{商品id} and num - #{购买的商品数量} >= 0
     */
    void placeOrder1() throws InterruptedException;

    /**
     * 2、第2种方式解决超卖，使用递增的版本解决，也就是大家常说的乐观锁
     *
     * @throws InterruptedException
     */
    void placeOrder2() throws InterruptedException;

    /**
     * 3、第3种方式解决超卖，使用通用的辅助表解决
     *
     * @throws InterruptedException
     */
    void placeOrder3() throws InterruptedException;

    /**
     * 4、第4种方式解决超卖，使用通用的辅助表解决
     *
     * @throws InterruptedException
     */
    void placeOrder4() throws InterruptedException;
    /**
     * 4、第4种方式解决超卖，事务加锁
     *
     * @throws InterruptedException
     */
    void placeOrder5() throws InterruptedException;

}
