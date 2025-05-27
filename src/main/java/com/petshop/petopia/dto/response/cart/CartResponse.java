package com.petshop.petopia.dto.response.cart;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartResponse {
    private Integer id;
    private List<CartItemResponse> items;
    private BigDecimal totalPrice;
}
