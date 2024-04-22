package com.muzi.part4.po;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

/**
 * 并发安全辅助表
 */
@Data
@TableName("t_concurrency_safe")
public class ConcurrencySafePO {

    @TableId
    private String id;

    /**
     * 需要确保数据安全性的唯一的key，由业务调用方提供，可以是 PO完整类名:id
     */
    private String safeKey;

    @Version
    private Long version;
}
