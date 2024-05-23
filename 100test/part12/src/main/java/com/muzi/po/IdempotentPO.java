package com.muzi.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@TableName("t_idempotent")
@Data
public class IdempotentPO {
    //id，主键
    private String id;

    //幂等key，唯一
    private String idempotentKey;
}
