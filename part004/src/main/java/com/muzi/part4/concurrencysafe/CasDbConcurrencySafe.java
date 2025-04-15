package com.muzi.part4.concurrencysafe;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.muzi.part4.mapper.ConcurrencySafeMapper;
import com.muzi.part4.po.ConcurrencySafePO;
import com.muzi.part4.utils.IdUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 通过乐观锁版本号version(乐观锁)确保修改数据的安全性
 */
@Component
public class CasDbConcurrencySafe implements DbConcurrencySafe {

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private ConcurrencySafeMapper concurrencySafeMapper;

    private ConcurrencySafePO getAndCreate(String key) {
        ConcurrencySafePO po = this.getByKey(key);
        if (po == null) {
            po = new ConcurrencySafePO();
            po.setId(IdUtils.generateId());
            po.setSafeKey(key);
            po.setVersion(0L);
            this.concurrencySafeMapper.insert(po);
        } else {
            ConcurrencySafePO clonePO = new ConcurrencySafePO();
            BeanUtils.copyProperties(po, clonePO);
            po = clonePO;
        }
        return po;
    }

    private ConcurrencySafePO getByKey(String key) {
        LambdaQueryWrapper<ConcurrencySafePO> query = Wrappers.lambdaQuery(ConcurrencySafePO.class).eq(ConcurrencySafePO::getSafeKey, key);
        return this.concurrencySafeMapper.selectOne(query);
    }

    @Override
    public <T> T exec(String key, Supplier<T> callback, Consumer<T> successCallBack, Consumer<ConcurrencyFailException> failCallBack) {
        return transactionTemplate.execute(status -> {
            //1、获取 ConcurrencySafePO
            ConcurrencySafePO po = this.getAndCreate(key);

            //2、执行业务操作
            T result = callback.get();

            //3、乐观锁更新 ConcurrencySafePO
            int updateCount = this.concurrencySafeMapper.optimisticUpdate(po);

            //成功执行回调
            if (updateCount == 1 && successCallBack != null) {
                successCallBack.accept(result);
            }
            //updateCount==0，说明这个期间，数据被人修改了
            if (updateCount == 0) {
                //失败，创建一个异常
                ConcurrencyFailException concurrencyFailException = new ConcurrencyFailException(key, "并发修改失败!");

                //若调用方传入了失败回调的函数failCallBack，那么将执行回调
                if (failCallBack != null) {
                    failCallBack.accept(concurrencyFailException);
                } else {
                    //兜底，抛出异常，让事务回滚
                    throw concurrencyFailException;
                }
            }
            return result;
        });
    }

}
