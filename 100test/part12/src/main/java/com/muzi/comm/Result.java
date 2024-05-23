package com.muzi.comm;

import lombok.Data;


@Data
public class Result<T> {
    /**
     * 编码,1：成功，其他值失败
     */
    private String code;
    /**
     * 结果
     */
    public T data;
    /**
     * 提示消息
     */
    private String msg;
}
