package com.petshop.petopia.controller;

import com.petshop.petopia.security.JwtService;
import com.petshop.petopia.service.OrderService;
import com.petshop.petopia.service.PaymentService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import vn.payos.PayOS;
import vn.payos.type.PaymentLinkData;
import vn.payos.type.Webhook;
import vn.payos.type.WebhookData;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    private final JwtService jwtService;
    private final OrderService orderService;
    private final PaymentService paymentService;

    public PaymentController(JwtService jwtService, OrderService orderService, PaymentService paymentService) {
        this.jwtService = jwtService;
        this.orderService = orderService;
        this.paymentService = paymentService;
    }


    @PostMapping("/payos_transfer_handler")
    public ObjectNode handlePayosWebhook(@RequestBody ObjectNode body) {
        return paymentService.handlePayosTransferWebhook(body);
    }

    @GetMapping(path = "/{orderCode}")
    public ObjectNode getPaymentById(
            @PathVariable("orderCode") Long orderCode,
            @RequestHeader("Authorization") String token) {
        Integer userId = jwtService.extractUserId(token);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        try {
            PaymentLinkData order = orderService.getPaymentDetails(orderCode, userId);

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
