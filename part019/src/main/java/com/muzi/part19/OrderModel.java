package com.muzi.part19;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OrderModel {
    //订单id
    private String id;
    //订单状态
    private int status;

}
