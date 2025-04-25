package com.petshop.petopia.dto.request.cart;

import lombok.Data;

@Data
public class CartItemUpdateRequest {
    private Integer itemId;
    private Integer quantity;
}