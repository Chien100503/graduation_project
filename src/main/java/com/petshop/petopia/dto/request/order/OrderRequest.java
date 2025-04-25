package com.petshop.petopia.dto.request.order;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private String shippingAddress;
    private String paymentMethod;
    private String phoneNumber;
    private List<OrderItemRequest> items;
}