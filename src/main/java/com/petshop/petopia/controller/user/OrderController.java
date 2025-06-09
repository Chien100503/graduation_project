package com.petshop.petopia.controller.user;

import com.petshop.petopia.dto.request.order.CreateOrderRequest;
import com.petshop.petopia.dto.response.order.OrderHistoryResponse;
import com.petshop.petopia.security.JwtService;
import com.petshop.petopia.service.user.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.node.ObjectNode;
import vn.payos.type.PaymentLinkData;

import java.util.List;

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

    @PostMapping("/cancel/{orderCode}")
    public ResponseEntity<Void> cancelOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long orderCode) {
        try {
            Integer userId = jwtService.extractUserId(token);
            orderService.cancelOrder(orderCode, userId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getOrderHistory(@RequestHeader("Authorization") String token) {
        Integer userId = jwtService.extractUserId(token);
        List<OrderHistoryResponse> history = orderService.getOrderHistoryByUser(userId);
        return ResponseEntity.ok(history);
    }

}
