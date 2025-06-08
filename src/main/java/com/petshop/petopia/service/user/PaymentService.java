package com.petshop.petopia.service.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.petshop.petopia.component.Global;
import com.petshop.petopia.model.order.Order;
import com.petshop.petopia.model.order.Payment;
import com.petshop.petopia.repository.order.OrderRepository;
import com.petshop.petopia.repository.order.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.type.Webhook;
import vn.payos.type.WebhookData;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PayOS payOS;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public ObjectNode handlePayosTransferWebhook(ObjectNode body) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();

        try {
            Webhook webhookBody = objectMapper.treeToValue(body, Webhook.class);
            WebhookData webhookData = payOS.verifyPaymentWebhookData(webhookBody);

            System.out.println(objectMapper.valueToTree(webhookData));

            if ("00".equals(webhookData.getCode())) {
                Long orderCode = webhookData.getOrderCode();

                if (orderCode != null) {
                    Optional<Payment> optionalPayment = paymentRepository.findByOrderCode(orderCode);

                    if (optionalPayment.isPresent()) {
                        Payment payment = optionalPayment.get();
                        Order order = payment.getOrder();
                        if (order != null) {
                            order.setStatus(Global.OrderStatus.CONFIRMED);
                            order.setPaid(true);
                            orderRepository.save(order);
                        }
                        paymentRepository.save(payment);
                    } else {
                        System.err.println("Không tìm thấy payment với orderCode: " + orderCode);
                    }
                }
            }

            response.put("error", 0);
            response.put("message", "Webhook delivered");
            response.set("data", objectMapper.valueToTree(webhookData));
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
            return response;
        }
    }
}
