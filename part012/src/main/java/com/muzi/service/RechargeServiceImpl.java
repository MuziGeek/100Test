package com.muzi.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.muzi.comm.ServiceExceptionUtils;
import com.muzi.mapper.IdempotentMapper;
import com.muzi.mapper.RechargeMapper;
import com.muzi.po.AccountPO;
import com.muzi.po.IdempotentPO;
import com.muzi.po.RechargePO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;


@Primary
@Service
public class RechargeServiceImpl extends ServiceImpl<RechargeMapper, RechargePO> implements RechargeService {
    @Autowired
    private RechargeMapper rechargeMapper;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private IdempotentMapper idempotentMapper;


    @Override
    public boolean reset(String rechargeId) {
        RechargePO rechargePO = this.getById(rechargeId);
        //将状态置为0
        this.lambdaUpdate()
                .set(RechargePO::getStatus, 0)
                .eq(RechargePO::getId, rechargeId)
                .update();
        //将账户余额更新为0
        this.accountService.lambdaUpdate()
                .set(AccountPO::getBalance, new BigDecimal("0.00"))
                .eq(AccountPO::getId, rechargePO.getAccountId()).update();

        //幂等表业清一下
        this.idempotentMapper.delete(Wrappers.lambdaQuery(IdempotentPO.class).eq(IdempotentPO::getIdempotentKey, String.format("%s:%s", rechargeId, "RECHARGE_SUCCESS")));
        return true;
    }

    @Override
    public boolean rechargeCallBack1(String rechargeId) {
        RechargePO rechargePO = this.getById(rechargeId);
        if (rechargePO == null) {
            throw ServiceExceptionUtils.exception("未找到充值记录");
        }
        //已处理成功，直接返回
        if (rechargePO.getStatus() == 1) {
            return true;
        }
        this.transactionTemplate.executeWithoutResult(action -> {
            //update updateRechargeSuccess set status = 1 where id = #{rechargeId} and status = 0
            int updateCount = this.rechargeMapper.updateRechargeSuccess(rechargeId);
            //updateCount!=1 表示未成功
            if (updateCount != 1) {
                throw ServiceExceptionUtils.exception("系统繁忙，请稍后重试");
            }
            //更新账户余额
            this.accountService.balanceAdd(rechargePO.getAccountId(), rechargePO.getPrice());
        });
        return true;
    }

    @Override
    public boolean rechargeCallBack2(String rechargeId) {
        RechargePO rechargePO = this.getById(rechargeId);
        if (rechargePO == null) {
            throw ServiceExceptionUtils.exception("未找到充值记录");
        }
        //已处理成功，直接返回
        if (rechargePO.getStatus() == 1) {
            return true;
        }
        //期望版本号
        Long expectVersion = rechargePO.getVersion();
        this.transactionTemplate.executeWithoutResult(action -> {
            //update t_recharge set status = 1 where id = #{rechargeId} and status = 0
            int updateCount = this.rechargeMapper.updateRechargeSuccessOptimisticLock(rechargeId, expectVersion);
            //updateCount!=1 表示未成功
            if (updateCount != 1) {
                throw ServiceExceptionUtils.exception("系统繁忙，请稍后重试");
            }
            //更新账户余额
            this.accountService.balanceAdd(rechargePO.getAccountId(), rechargePO.getPrice());
        });
        return true;
    }

    @Autowired
    private IdempotentService idempotentService;

    @Override
    public boolean rechargeCallBack3(String rechargeId) {
        RechargePO rechargePO = this.getById(rechargeId);
        if (rechargePO == null) {
            throw ServiceExceptionUtils.exception("未找到充值记录");
        }
        //已处理成功，直接返回
        if (rechargePO.getStatus() == 1) {
            return true;
        }
        //使用幂等工具进行处理
        this.idempotentService.idempotent(rechargeId, "RECHARGE_SUCCESS", () -> {
            //将充值订单更新为成功
            rechargePO.setStatus(1);
            boolean update = this.updateById(rechargePO);
            if (!update) {
                throw ServiceExceptionUtils.exception("充值记录更新失败");
            }
            //更新账户余额
            this.accountService.balanceAdd(rechargePO.getAccountId(), rechargePO.getPrice());
        });
        return true;
    }
}
