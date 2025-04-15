package com.muzi.service;



import com.baomidou.mybatisplus.extension.service.IService;
import com.muzi.po.AccountPO;

import java.math.BigDecimal;

/**
 * 账户服务
 * <b>description</b>： Java高并发、微服务、性能优化实战案例100讲，视频号：程序员路人，源码 & 文档 & 技术支持，请加个人微信号：itsoku <br>
 * <b>time</b>：2024/4/4 23:13 <br>
 * <b>author</b>：ready likun_557@163.com
 */
public interface AccountService extends IService<AccountPO> {
    /**
     * 余额增加price
     *
     * @param accountId 用户账号id
     * @param price     金额，必须大于0
     */
    void balanceAdd(String accountId, BigDecimal price);
}
