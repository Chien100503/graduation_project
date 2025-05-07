package com.petshop.petopia.dto.request.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPendingInfo {
    private String orderId; // orderCode từ PayOS
    private Integer userId;
}