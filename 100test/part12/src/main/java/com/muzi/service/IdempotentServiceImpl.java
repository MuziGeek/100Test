package com.muzi.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import com.muzi.mapper.IdempotentMapper;
import com.muzi.po.IdempotentPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Primary
@Service
public class IdempotentServiceImpl implements IdempotentService {
    @Autowired
    private IdempotentMapper idempotentMapper;
    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    public int idempotent(String idempotentKey, Runnable r) {
        //1.根据 idempotentKey 查找记录，如果能找到，说明业务已成功处理过
        IdempotentPO idempotentPO = this.getByIdempotentKey(idempotentKey);
        if (idempotentPO != null) {
            //已处理过返回-1
            return -1;
        }
        //这块一定要通过事务包裹起来
        this.transactionTemplate.executeWithoutResult(action -> {
            //2.执行业务
            r.run();

            /**
             * 3.向幂等表插入数据
             * 如果这个地方有并发，则由于（t_idempotent.idempotent_key）的唯一性，会导致有一个会执行失败，抛出异常，导致事务回滚
             */
            IdempotentPO po = new IdempotentPO();
            po.setId(IdUtil.fastSimpleUUID());
            po.setIdempotentKey(idempotentKey);
            this.idempotentMapper.insert(po);
        });
        //成功处理返回1
        return 1;
    }

    private IdempotentPO getByIdempotentKey(String idempotentKey) {
        LambdaQueryWrapper<IdempotentPO> qw = Wrappers.lambdaQuery(IdempotentPO.class).eq(IdempotentPO::getIdempotentKey, idempotentKey);
        return this.idempotentMapper.selectOne(qw);
    }
}
