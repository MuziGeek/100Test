package com.muzi.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;


@TableName("t_account")
@Data
public class AccountPO {
    //账户id
    private String id;

    //账户名
    private String name;

    //账户余额
    private BigDecimal balance;
}
