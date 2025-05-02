package com.petshop.petopia.dto.request.order;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    private List<OrderItemRequest> items;
    private String shippingAddress;
    private String phoneNumber;
    private PaymentRequest paymentRequest;
}