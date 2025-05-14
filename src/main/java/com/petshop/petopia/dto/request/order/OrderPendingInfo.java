package com.petshop.petopia.dto.request.order;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderPendingInfo {
    private String orderId;
    private Integer userId;
}