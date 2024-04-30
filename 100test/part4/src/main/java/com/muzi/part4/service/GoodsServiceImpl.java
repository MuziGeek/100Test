package com.muzi.part4.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muzi.part4.concurrencysafe.ConcurrencyFailException;
import com.muzi.part4.concurrencysafe.DbConcurrencySafe;
import com.muzi.part4.mapper.GoodsMapper;
import com.muzi.part4.po.GoodsPO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

@Service
@Slf4j
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, GoodsPO> implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private DbConcurrencySafe dbConcurrencySafe;

    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 方案1：通过update中携带条件判断解决超卖问题
     *
     * @throws InterruptedException
     */
    @Override
    public void placeOrder1() throws InterruptedException {
        //扣减库存的方法，返回值为1表示扣减库存成功，0表示失败
        Function<String, Integer> reduceStock = (String goodsId) -> {
            int update = goodsMapper.placeOrder1(goodsId, 1);
            return update;
        };
        //模拟100人秒杀
        this.concurrentPlaceOrderMock("方案1", reduceStock);
    }

    /**
     * 方案2：乐观锁解决超卖问题
     *
     * @throws InterruptedException
     */
    @Override
    public void placeOrder2() throws InterruptedException {
        //扣减库存的方法，返回值为1表示扣减库存成功，0表示失败
        Function<String, Integer> reduceStock = (String goodsId) -> {
            //1、先查询
            GoodsPO goodsPO = this.getById(goodsId);

            //2、判断库存是否==0，则直接返回失败
            if (goodsPO.getNum() == 0) {
                return 0;
            }
            //3.库存看起来够，但是并发的时候可能就不够了，下面带版本号更新库存，判断影响行数，update 为1表示成功，0表示扣减库存失败
            int update = goodsMapper.placeOrder2(goodsId, 1, goodsPO.getVersion());
            return update;
        };
        //模拟100人秒杀
        this.concurrentPlaceOrderMock("方案2", reduceStock);
    }

    /**
     * 方案3：对比数据修改前后是否和期望的一致，解决超卖问题
     *
     * @throws InterruptedException
     */
    @Override
    public void placeOrder3() throws InterruptedException {
        //扣减库存的方法，返回值为1表示扣减库存成功，0表示失败
        Function<String, Integer> reduceStock = (String goodsId) -> {
            //1、根据商品id获商品
            GoodsPO updateBeforeGoods = this.getById(goodsId);

            //2、判断库存是否够
            if (updateBeforeGoods.getNum() == 0) {
                return 0;
            }
            //启动事务操作扣减库存
            int reduceStockResult = this.transactionTemplate.execute(action -> {

                //3、执行更新扣减库存
                this.goodsMapper.placeOrder3(goodsId, 1);

                //4、修改数据完成后，查出来看一下，和期望的结果是不是一致的，如果是，表示成功，否则失败
                GoodsPO updateAfterGoods = this.getById(goodsId);

                //5、判断：库存扣减前的数量是否等于 扣减后库存数量+1,如果
                if (updateBeforeGoods.getNum() - 1 != updateAfterGoods.getNum()) {
                    //设置事务回滚
                    action.setRollbackOnly();
                    return 0;
                } else {
                    //成功
                    return 1;
                }
            });
            return reduceStockResult;
        };
        //模拟100人秒杀
        this.concurrentPlaceOrderMock("方案3", reduceStock);
    }

    /**
     * 方案4：通过辅助类解决超卖问题，这种本质上可以解决所有并发修改db数据出错的问题
     *
     * @throws InterruptedException
     */
    @Override
    public void placeOrder4() throws InterruptedException {
        //扣减库存的方法，返回值为1表示扣减库存成功，0表示失败
        Function<String, Integer> reduceStock = (String goodsId) -> {
            try {
                //使用 dbConcurrencySafe.exec 包住需要并发操作的数据，可以确保数据修改的安全性
                return this.dbConcurrencySafe.exec(GoodsPO.class, goodsId, () -> {
                    //1、根据商品id获商品
                    GoodsPO goodsPO = this.getById(goodsId);

                    //2、判断库存是否够
                    if (goodsPO.getNum() == 0) {
                        return 0;
                    }
                    //3、执行更新扣减库存
                    this.goodsMapper.placeOrder3(goodsId, 1);
                    return 1;
                });
            } catch (ConcurrencyFailException e) {
                return 0;
            } catch (Exception e) {
                return 0;
            }
        };
        //模拟100人秒杀
        this.concurrentPlaceOrderMock("方案4", reduceStock);
    }
    /**
     * 方案5 事务加锁
     *
     */
    @Transactional
    @Override
    public void placeOrder5() throws InterruptedException {
        //扣减库存的方法，返回值为1表示扣减库存成功，0表示失败
        Function<String, Integer> reduceStock = (String goodsId) -> {
            int update = goodsMapper.placeOrder1(goodsId, 1);
            return update;
        };
        //模拟100人秒杀
        this.concurrentPlaceOrderMock("方案1", reduceStock);

    }


    /**
     * 模拟100人秒杀请求
     *
     * @param fun 扣减库存的函数，fun函数的参数为商品id，返回值：1：表示抢购成功，0：表示抢购失败
     * @throws InterruptedException
     */
    private void concurrentPlaceOrderMock(String method, Function<String, Integer> fun) throws InterruptedException {
        //1、初始化一条商品记录 [商品Id:1,名称：iphone、库存：10]
        String goodsId = "1", goodsName = "iphone";
        // 库存10个
        int num = 10;

        GoodsPO goodsStart = this.initTestData(goodsId, goodsName, num);

        //2、创建线程池，大小为100，模拟100个线程并发下单
        int concurrentNum = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(concurrentNum);
        CountDownLatch countDownLatch = new CountDownLatch(concurrentNum);
        AtomicInteger successNum = new AtomicInteger(0);
        AtomicInteger failNum = new AtomicInteger(0);

        //使用线程池模拟100人抢购
        for (int i = 0; i < concurrentNum; i++) {
            executorService.execute(() -> {
                try {
                    // 调用抢购的函数，update表示抢购的结果，1：表示抢购成功，0：抢购失败
                    int update = fun.apply(goodsId);

                    //update=0,表示更新失败，否则表示更新成功
                    if (update == 0) {
                        //抢购失败，失败次数+1，业务上在这里可能要做一些事情（如让事务回滚，那么可以在此抛出一个异常，spring事务会自动回滚）
                        failNum.incrementAndGet();
                    } else {
                        //更新成功，成功次数+1
                        successNum.incrementAndGet();
                    }
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        // 调用下面的方法，等待上面线程池中的任务都执行完毕后，才会继续向下走
        countDownLatch.await();
        // 模拟100人抢购结束，获取抢购完毕后商品的信息
        GoodsPO goodsEnd = this.getById(goodsId);

        //抢购完毕，输出日志，方便看效果，输出商品目前的信息，下单成功的数量，失败的数量
        System.out.println(String.format("===========================解决超卖，%s 开始执行=======================================", method));

        System.out.println(String.format("模拟 %s 人进行抢购", concurrentNum));
        System.out.println("抢购结束啦............\n");
        System.out.println(String.format("抢购前，商品库存：%s", goodsStart.getNum()));
        System.out.println(String.format("抢购后，商品库存：%s", goodsEnd.getNum()));
        System.out.println(String.format("下单成功人数：%s", successNum.get()));
        System.out.println(String.format("下单失败人数：%s", failNum.get()));

        System.out.println(String.format("===========================解决超卖，%s 执行结束=======================================", method));
    }

    private GoodsPO initTestData(String goodsId, String goodsName, int num) {
        //清理下这个商品
        this.removeById(goodsId);

        //重新插入，方便测试
        GoodsPO goodsPO = new GoodsPO();
        goodsPO.setGoodsId(goodsId);
        goodsPO.setGoodsName(goodsName);
        goodsPO.setNum(num);
        this.save(goodsPO);

        return goodsPO;
    }

    public static void main(String[] args) {
        String str1 =new StringBuilder("py").append("thon").toString();
        System.out.println(str1.intern()==str1);

    }
}
