package com.muzi.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muzi.comm.ServiceExceptionUtils;
import com.muzi.mapper.AccountMapper;
import com.muzi.po.AccountPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Primary
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, AccountPO> implements AccountService {
    @Autowired
    private AccountMapper accountMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void balanceAdd(String accountId, BigDecimal price) {
        int updateCount = accountMapper.balanceAdd(accountId, price);
        if (updateCount != 1) {
            throw ServiceExceptionUtils.exception("更新账户余额失败!");
        }
    }
}
