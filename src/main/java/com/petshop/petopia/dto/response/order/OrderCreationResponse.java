package com.petshop.petopia.dto.response.order;

import com.petshop.petopia.model.order.Order;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreationResponse {
    private Order order;
    private String paymentLink;
    private String message;
}