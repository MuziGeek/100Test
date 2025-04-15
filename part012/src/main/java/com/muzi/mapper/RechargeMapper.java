package com.muzi.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.muzi.po.RechargePO;
import org.apache.ibatis.annotations.Param;


public interface RechargeMapper extends BaseMapper<RechargePO> {
    /**
     * 将充值记录状态更新为成功（将status作为条件判断的方式）
     *
     * @param rechargeId
     * @return
     */
    int updateRechargeSuccess(@Param("rechargeId") String rechargeId);

    /**
     * 将充值记录状态更新为成功（乐观锁的方式）
     *
     * @param rechargeId
     * @param expectVersion 期望版本号
     * @return
     */
    int updateRechargeSuccessOptimisticLock(@Param("rechargeId") String rechargeId, @Param("expectVersion") Long expectVersion);
}
