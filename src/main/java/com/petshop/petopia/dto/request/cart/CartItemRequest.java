package com.petshop.petopia.dto.request.cart;

import lombok.Data;

@Data
public class CartItemRequest {
    private Integer petId;
    private Integer productId;
    private Integer quantity;
}