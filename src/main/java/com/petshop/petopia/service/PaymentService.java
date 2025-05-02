package com.petshop.petopia.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.petshop.petopia.model.order.Payment;
import com.petshop.petopia.repository.order.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.type.Webhook;
import vn.payos.type.WebhookData;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PayOS payOS;
    private final PaymentRepository paymentRepository;

    public ObjectNode handlePayosTransferWebhook(ObjectNode body) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();

        try {
            Webhook webhookBody = objectMapper.treeToValue(body, Webhook.class);
            WebhookData webhookData = payOS.verifyPaymentWebhookData(webhookBody);

            System.out.println(webhookData);

            response.put("error", 0);
            response.put("message", "Webhook delivered");
            response.set("data", null);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
        }

        return response;
    }

    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }
}
