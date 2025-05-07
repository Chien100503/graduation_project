package com.petshop.petopia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petshop.petopia.dto.request.order.CreateOrderRequest;
import com.petshop.petopia.model.order.Order;
import com.petshop.petopia.security.JwtService;
import com.petshop.petopia.service.OrderService;
import com.petshop.petopia.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;
import vn.payos.type.PaymentLinkData;

import static com.google.cloud.firestore.telemetry.MetricsUtil.logger;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final JwtService jwtService;
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @PostMapping("/create")
    public ObjectNode createOrder(
            @RequestBody CreateOrderRequest request,
            @RequestHeader("Authorization") String token) {
        Integer userId = jwtService.extractUserId(token);
        return orderService.createOrder(request, userId);
    }

    @PostMapping("/payos_transfer_handler")
    public ObjectNode handlePayosWebhook(@RequestBody ObjectNode body) {
        return paymentService.handlePayosTransferWebhook(body);
    }

    @GetMapping(path = "/{orderId}")
    public ObjectNode getOrderById(
            @PathVariable("orderId") Long orderId,
            @RequestHeader("Authorization") String token) {
        Integer userId = jwtService.extractUserId(token);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();

        try {
            PaymentLinkData order = orderService.getOrderDetails(orderId, userId);

            response.set("data", objectMapper.valueToTree(order));
            response.put("error", 0);
            response.put("message", "ok");
            return response;

        } catch (AccessDeniedException e) {
            e.printStackTrace();
            response.put("error", 403);
            response.put("message", e.getMessage());
            response.set("data", null);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", "Internal Server Error: " + e.getMessage());
            response.set("data", null);
            return response;
        }
    }
}
