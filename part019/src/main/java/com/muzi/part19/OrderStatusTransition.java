package com.muzi.part19;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class OrderStatusTransition {
    //当前状态
    private OrderStatus fromStatus;
    //动作
    private  OrderStatusChanegeAction action;
    //目标状态

    private OrderStatus toStatus;
}
