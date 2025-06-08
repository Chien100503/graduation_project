package com.petshop.petopia.dto.response.order;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemResponse {
    private Integer id;
    private String thumbnailUrl;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
}