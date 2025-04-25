package com.petshop.petopia.controller;

import com.petshop.petopia.dto.request.order.OrderRequest;
import com.petshop.petopia.model.order.Order;
import com.petshop.petopia.security.JwtService;
import com.petshop.petopia.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final JwtService jwtService; // Inject JwtService

    @PostMapping("/place")
    public ResponseEntity<Order> placeOrder(
            @RequestBody OrderRequest orderRequest,
            @RequestHeader("Authorization") String token
    ) {

            Integer userId = jwtService.extractUserId(token);

            if (userId != null) {
                Order placedOrder = orderService.placeOrder(
                        userId,
                        orderRequest.getShippingAddress(),
                        orderRequest.getPhoneNumber()
                );
                return new ResponseEntity<>(placedOrder, HttpStatus.CREATED);
            } else {
                // Xử lý trường hợp không thể trích xuất userId từ token (ví dụ: token không hợp lệ)
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

    }
}