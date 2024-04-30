package com.muzi.part4.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.muzi.part4.po.ConcurrencySafePO;


public interface ConcurrencySafeMapper extends BaseMapper<ConcurrencySafePO> {

    /**
     * 乐观锁更新 ConcurrencySafePO
     *
     * @param po
     * @return
     */
    int optimisticUpdate(ConcurrencySafePO po);
}
