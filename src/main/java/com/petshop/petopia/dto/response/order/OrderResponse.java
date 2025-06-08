package com.petshop.petopia.dto.response.order;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderResponse {
    private Integer id;
    private List<OrderItemResponse> items;
    private BigDecimal totalPrice;
}