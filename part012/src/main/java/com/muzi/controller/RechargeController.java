package com.muzi.controller;


import com.muzi.po.AccountPO;
import com.muzi.po.RechargePO;
import com.muzi.service.AccountService;
import com.muzi.service.RechargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class RechargeController {
    @Autowired
    private RechargeService rechargeService;
    @Autowired
    private AccountService accountService;

    /**
     * 账户和充值记录信息，用户测试用的
     *
     * @param rechargeId
     * @return
     */
    @GetMapping("accountAndRechargeInfo")
    public String accountAndRechargeInfo(@RequestParam("rechargeId") String rechargeId) {
        RechargePO rechargePO = this.rechargeService.getById(rechargeId);
        AccountPO accountPo = this.accountService.getById(rechargePO.getAccountId());
        return String.format("充值订单号：%s，状态：%s，账户余额：%s", rechargeId, rechargePO.getStatus(), accountPo.getBalance());
    }

    /**
     * 重置一下数据，将充值订单状态置为0，账户余额置为0
     *
     * @param rechargeId
     * @return
     */
    @GetMapping("/reset")
    public boolean reset(@RequestParam("rechargeId") String rechargeId) {
        return this.rechargeService.reset(rechargeId);
    }


    /**
     * 充值回调，处理方式1：更新时使用本身状态作为条件判断解决
     *
     * @param rechargeId
     * @return 若已处理成功过或处理成功，返回true，否则返回false
     */
    @PostMapping("/rechargeCallBack1")
    public boolean rechargeCallBack1(@RequestParam("rechargeId") String rechargeId) {
        return this.rechargeService.rechargeCallBack1(rechargeId);
    }

    /**
     * 充值回调，处理方式2：采用乐观锁解决
     *
     * @param rechargeId
     * @return 若已处理成功过或处理成功，返回true，否则返回false
     */
    @PostMapping("/rechargeCallBack2")
    public boolean rechargeCallBack2(@RequestParam("rechargeId") String rechargeId) {
        return this.rechargeService.rechargeCallBack2(rechargeId);
    }

    /**
     * 充值回调，处理方式3：使用唯一约束解决
     *
     * @param rechargeId
     * @return 若已处理成功过或处理成功，返回true，否则返回false
     */
    @PostMapping("/rechargeCallBack3")
    public boolean rechargeCallBack3(@RequestParam("rechargeId") String rechargeId) {
        return this.rechargeService.rechargeCallBack3(rechargeId);
    }


}
