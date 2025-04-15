package com.muzi.part19;

import java.util.Objects;

public enum OrderStatus {
    INIT(0,"待支付"),
    PAID(100,"已付款"),
    SHIPPED(200,"已发货"),
    FINISHED(300,"已结束");
    
    private int status;
    private String desc;

    OrderStatus(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public int getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
    public static OrderStatus get(Integer status){
        for (OrderStatus value:
             values()) {
            if (Objects.equals(value.getStatus(),status)){
                return value;
            }
            
        }
        return null;
    }
}
