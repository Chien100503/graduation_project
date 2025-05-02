package com.petshop.petopia.dto.response.order;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class OrderResponse {
    private Integer id;
    private Long userId; // Sử dụng Long để phù hợp với kiểu dữ liệu của User
    private List<OrderItemResponse> items;
    private Date orderDate;
    private Integer totalPrice;
    private String shippingAddress;
    private String phoneNumber;
    private PaymentResponse paymentResponse;
}