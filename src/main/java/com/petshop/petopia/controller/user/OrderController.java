package com.petshop.petopia.controller.user;

import com.petshop.petopia.dto.request.order.CreateOrderRequest;
import com.petshop.petopia.security.JwtService;
import com.petshop.petopia.service.user.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final JwtService jwtService;

    @PostMapping("/create")
    public ObjectNode createOrder(
            @RequestBody CreateOrderRequest request,
            @RequestHeader("Authorization") String token) {
        Integer userId = jwtService.extractUserId(token);
        return orderService.createOrder(request, userId);
    }

}
