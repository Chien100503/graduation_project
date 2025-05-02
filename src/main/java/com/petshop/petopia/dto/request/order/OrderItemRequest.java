package com.petshop.petopia.dto.request.order;

import lombok.Data;

import java.util.List;

@Data
public class OrderItemRequest {
    private Integer productId;
    private Integer petId;
    private Integer quantity;
}