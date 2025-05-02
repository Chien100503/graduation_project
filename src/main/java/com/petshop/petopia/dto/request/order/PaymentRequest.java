package com.petshop.petopia.dto.request.order;

import lombok.Data;

@Data
public class PaymentRequest {
    private String paymentMethod;
}