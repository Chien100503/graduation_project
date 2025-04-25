package com.petshop.petopia.dto.request.order;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {
    private Integer productId;
    private Integer petId;
    private int quantity;
    private double price;
}