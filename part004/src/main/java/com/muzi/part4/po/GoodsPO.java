package com.muzi.part4.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@Data
@TableName("t_goods")
public class GoodsPO {
    /**
     * 商品id
     */
    @TableId
    private String goodsId;

    /**
     * 文件名称
     */

    private String goodsName;

    /**
     * 商品库存
     */
    private Integer num;

    /**
     * 版本号
     */
    private Long version;
}
