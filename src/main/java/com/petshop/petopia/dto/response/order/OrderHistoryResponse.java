package com.petshop.petopia.dto.response.order;

import com.petshop.petopia.component.Global;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderHistoryResponse {
    private Integer orderId;
    private String recipientName;
    private String shippingAddress;
    private String phoneNumber;
    private BigDecimal totalPrice;
    private Global.OrderStatus status;
    private Global.PaymentMethod paymentMethod;
    private Date orderDate;
}
