package com.muzi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.muzi.po.AccountPO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

public interface AccountMapper extends BaseMapper<AccountPO> {
    int balanceAdd(@Param("accountId") String accountId, @Param("price") BigDecimal price);
}
