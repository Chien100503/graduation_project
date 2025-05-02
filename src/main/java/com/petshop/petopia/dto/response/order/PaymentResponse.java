package com.petshop.petopia.dto.response.order;

import lombok.Data;

import java.util.Date;

@Data
public class PaymentResponse {
    private Integer id;
    private String paymentMethod;
    private String transactionId;
    private Double amount;
    private Date paymentDate;
    private Boolean paymentStatus;
    private String payosOrderId;
}