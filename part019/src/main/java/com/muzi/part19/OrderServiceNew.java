package com.muzi.part19;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderServiceNew {
    /**
     * 订单状态转换列表，相当于订单的状态图存储再这个列表中了
     * 列表中的每条记录对应状态图中的一个流转步骤，<OrderStatusTransition:表示一个流转步骤，由<fromstutas,action,tostatus>>
     */
    public static List<OrderStatusTransition> orderStatusTransitionList= new ArrayList<>();

    static {
        //下面根据订单状态图，将订单流转步骤添加到orderStatusTransitionList 中
        //待支付-支付-已支付
        orderStatusTransitionList.add(OrderStatusTransition.builder()
                .fromStatus(OrderStatus.INIT)
                .action(OrderStatusChanegeAction.PAY)
                .toStatus(OrderStatus.PAID).build());


        //已支付-发货-已发货

        orderStatusTransitionList.add(OrderStatusTransition.builder()
                .fromStatus(OrderStatus.PAID)
                .action(OrderStatusChanegeAction.SHIP)
                .toStatus(OrderStatus.SHIPPED).build());

        //已发货-买家收货-完成

        orderStatusTransitionList.add(OrderStatusTransition.builder()
                .fromStatus(OrderStatus.SHIPPED)
                .action(OrderStatusChanegeAction.DELIVER)
                .toStatus(OrderStatus.FINISHED).build());

    }

    /**
     * 订单支付
     *
     * @param orderModel
     */
    public void pay(OrderModel orderModel) {
        // 订单状态转换
        this.statusTransition(orderModel, OrderStatusChanegeAction.PAY);

        //todo: 其他操作，比如将订单数据保存到db
    }

    /**
     * 触发订单状态转换
     *
     * @param orderModel 订单
     * @param action     动作
     */
    private void statusTransition(OrderModel orderModel, OrderStatusChanegeAction action) {

        //根据订单当前状态 & 动作，去 orderStatusTransitionList 中找，可以找到对应的记录，说明当前操作是允许的；否则抛出异常
        OrderStatus fromStatus = OrderStatus.get(orderModel.getStatus());
        Optional<OrderStatusTransition> first = orderStatusTransitionList.stream().
                filter(orderStatusTransition -> orderStatusTransition.getFromStatus().equals(fromStatus) && orderStatusTransition.getAction().equals(action))
                .findFirst();
        if (!first.isPresent()) {
            throw new RuntimeException("订单状态不支持当前操作");
        }
        OrderStatusTransition orderStatusTransition = first.get();
        //切换订单状态
        orderModel.setStatus(orderStatusTransition.getToStatus().getStatus());
    }

    /**
     * 卖家发货
     *
     * @param orderModel
     */
    public void ship(OrderModel orderModel) {
        // 订单状态转换
        this.statusTransition(orderModel, OrderStatusChanegeAction.SHIP);

        //todo: 其他操作，比如将订单数据保存到db
    }

    /**
     * 买家确认收货
     *
     * @param orderModel
     */
    public void deliver(OrderModel orderModel) {
        // 订单状态转换
        this.statusTransition(orderModel, OrderStatusChanegeAction.DELIVER);

        //todo: 其他操作，比如将订单数据保存到db
    }




}
