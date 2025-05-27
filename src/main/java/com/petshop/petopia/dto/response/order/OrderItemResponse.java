package com.petshop.petopia.dto.response.order;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemResponse {
    private Integer id;
    private Integer productId;
    private Integer petId;
    private Integer quantity;
    private BigDecimal price;
}