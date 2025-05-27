package com.petshop.petopia.dto.request.order;

import com.petshop.petopia.component.Global;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
public class CreateOrderRequest {
    private String shippingAddress;
    private String phoneNumber;
    private Global.PaymentMethod paymentMethod;
    private List<OrderItemRequest> items;
}