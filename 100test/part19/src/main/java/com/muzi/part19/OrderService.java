package com.muzi.part19;

import java.util.Objects;

/**/
public class OrderService {

    public void pay(OrderModel orderModel) {
        //验证订单状态，订单当前状态必须是待支付状态，即新创建的订单
        if (!Objects.equals(orderModel.getStatus(), OrderStatus.INIT.getStatus())) {
            throw new RuntimeException("订单状态不支持当前操作");
        }
        //将订单状态置为已支付状态
        orderModel.setStatus(OrderStatus.PAID.getStatus());

        //todo:其他操作，将订单数据保存到DB
    }
    
    public void ship(OrderModel orderModel){
        //验证订单状态，当前状态为已支付状态
        if (!Objects.equals(orderModel.getStatus(), OrderStatus.PAID.getStatus())) {
            throw new RuntimeException("订单状态不支持当前操作");
        }
        //将订单状态置为已支付状态
        orderModel.setStatus(OrderStatus.SHIPPED.getStatus());

        //todo:其他操作，将订单数据保存到DB

    }

    public void deliver(OrderModel orderModel){
        if (!Objects.equals(orderModel.getStatus(), OrderStatus.SHIPPED.getStatus())) {
            throw new RuntimeException("订单状态不支持当前操作");
        }
        //将订单状态置为已支付状态
        orderModel.setStatus(OrderStatus.FINISHED.getStatus());

        //todo:其他操作，将订单数据保存到DB
    }
}


