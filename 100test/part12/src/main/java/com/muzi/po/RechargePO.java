package com.muzi.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;


@TableName("t_recharge")
@Data
public class RechargePO {
    //充值记录id
    private String id;

    //账户id
    private String accountId;

    //充值金额
    private BigDecimal price;

    //充值记录状态，0：处理中，1：充值成功
    private Integer status;

    //系统版本号，默认为0，每次更新+1，用于乐观锁
    private Long version;
}
