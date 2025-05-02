package com.petshop.petopia.dto.request.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petshop.petopia.model.order.PaymentMethod;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
public class CreateOrderRequest {
    private String shippingAddress;
    private String phoneNumber;
    private PaymentMethod paymentMethod;

    @JsonProperty("isFromCart")
    private boolean isFromCart;
    private List<OrderItemRequest> items;
}