package com.petshop.petopia.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.petshop.petopia.dto.request.order.CreateOrderRequest;
import com.petshop.petopia.dto.request.order.OrderItemRequest;
import com.petshop.petopia.model.order.*;
import com.petshop.petopia.model.pet.Pet;
import com.petshop.petopia.model.product.Product;
import com.petshop.petopia.model.user.User;
import com.petshop.petopia.repository.cart.CartItemRepository;
import com.petshop.petopia.repository.order.OrderItemRepository;
import com.petshop.petopia.repository.order.OrderRepository;
import com.petshop.petopia.repository.order.PaymentRepository;
import com.petshop.petopia.repository.pet.PetRepository;
import com.petshop.petopia.repository.product.ProductRepository;
import com.petshop.petopia.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PetRepository petRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final PayOS payOS;

    private static final int DEFAULT_DESCRIPTION_LENGTH = 8;

    @Transactional
    public ObjectNode createOrder(CreateOrderRequest request, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<OrderItem> orderItems = new ArrayList<>();
        if (request.isFromCart()) {
            orderItems = processCartOrderItems(userId);
        } else {
            if (request.getItems() == null || request.getItems().isEmpty()) {
                throw new RuntimeException("Order must contain at least one item.");
            }
            orderItems = processDirectOrderItems(null, request.getItems()); // order = null ở đây, vì logic check ở dưới
        }

        // Kiểm tra tính khả thi của đơn hàng TRƯỚC KHI thực hiện thanh toán.
        if (!isOrderValid(orderItems)) {
            throw new RuntimeException("Đơn hàng không hợp lệ. Vui lòng kiểm tra lại số lượng sản phẩm/thú cưng.");
        }

        // Tính tổng giá trị đơn hàng.
        Integer totalPrice = calculateTotalPrice(orderItems);

        // Nếu là thanh toán PayOS, chỉ cần trả về thông tin thanh toán.
        if (PaymentMethod.PAYOS.name().equalsIgnoreCase(request.getPaymentMethod().name())) {
            return handlePayOSPaymentPreview(orderItems, totalPrice); // Thêm totalPrice vào đây
        }

        // Tạo đơn hàng
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(request.getShippingAddress());
        order.setPhoneNumber(request.getPhoneNumber());
        order.setOrderDate(new Date());
        order.setPaid(false);
        order.setDelivered(false);
        order.setReceived(false);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setItems(orderItems);
        order.setTotalPrice(totalPrice); // Sử dụng totalPrice đã tính toán
        orderRepository.save(order);

        // Lưu các mục đơn hàng và cập nhật số lượng sản phẩm/trạng thái thú cưng.
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(order); // Set order cho từng item
            orderItemRepository.save(orderItem);
            if (orderItem.getProduct() != null) {
                Product product = orderItem.getProduct();
                product.setStockQuantity(product.getStockQuantity() - orderItem.getQuantity());
                productRepository.save(product);
            } else if (orderItem.getPet() != null) {
                Pet pet = orderItem.getPet();
                pet.setStatus(false);
                petRepository.save(pet);
            }
        }
        if (request.isFromCart()){
            cartItemRepository.deleteCartItemsByUserId(userId);
        }


        // Xử lý thanh toán COD.
        if (PaymentMethod.COD.name().equalsIgnoreCase(request.getPaymentMethod().name())) {
            return handleCodPayment(order);
        } else {
            throw new RuntimeException("Invalid payment method"); // Để các payment method khác tự xử lý
        }
    }

    private List<OrderItem> processCartOrderItems(Integer userId) {
        List<OrderItem> orderItems = cartItemRepository.findByUserId(userId).stream()
                .map(cartItem -> {
                    OrderItem item = new OrderItem();
                    //item.setOrder(order); // Không cần set order ở đây
                    item.setProduct(cartItem.getProduct());
                    item.setQuantity(cartItem.getQuantity());
                    item.setPrice(cartItem.getProduct() != null ? cartItem.getProduct().getPrice() : cartItem.getPet().getPrice()); // Thêm kiểm tra null
                    return item;
                }).collect(Collectors.toList());

        if (orderItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống. Không thể tạo đơn hàng.");
        }
        return orderItems;
    }

    private List<OrderItem> processDirectOrderItems(Order order, List<OrderItemRequest> itemRequests) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequest itemRequest : itemRequests) {
            OrderItem orderItem = new OrderItem();
            //orderItem.setOrder(order); // Không cần set order ở đây

            if (itemRequest.getProductId() != null) {
                Product product = productRepository.findById(itemRequest.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));
                if (itemRequest.getQuantity() == null || itemRequest.getQuantity() <= 0) {
                    throw new RuntimeException("Invalid quantity for product");
                }

                orderItem.setProduct(product);
                orderItem.setQuantity(itemRequest.getQuantity());
                orderItem.setPrice(product.getPrice());

            } else if (itemRequest.getPetId() != null) {
                Pet pet = petRepository.findById(itemRequest.getPetId())
                        .orElseThrow(() -> new RuntimeException("Pet not found"));

                orderItem.setPet(pet);
                orderItem.setQuantity(1);
                orderItem.setPrice(pet.getPrice());

            } else {
                throw new RuntimeException("Invalid direct order item: must be pet or product");
            }
            orderItems.add(orderItem);
        }
        return orderItems;
    }

    private Integer calculateTotalPrice(List<OrderItem> orderItems) {
        return orderItems.stream()
                .mapToInt(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    private ObjectNode handleCodPayment(Order order) {
        order.setPaid(false);

        Payment payment = new Payment();
        payment.setPaymentMethod(PaymentMethod.COD);
        payment.setPaymentDate(new Date());
        payment.setTransactionContent("COD-" + UUID.randomUUID());

        paymentRepository.save(payment);
        order.setPayment(payment);
        orderRepository.save(order);


        ObjectNode response = new ObjectMapper().createObjectNode();
        response.put("message", "Đặt hàng COD thành công");
        response.put("orderId", order.getId());
        return response;
    }

    private ObjectNode handlePayOSPaymentPreview(List<OrderItem> items, int totalPrice) { // Thêm totalPrice
        ObjectNode response = new ObjectMapper().createObjectNode();
        String returnUrl = "";
        String cancelUrl = "";
        String description = generatePaymentDescription();
        long orderCode = generateOrderCode();

        try {
            // int totalAmount = calculateTotalPrice(items);  // Không tính lại nữa.
            List<ItemData> payItems = items.stream()
                    .map(this::mapOrderItemToItemData)
                    .collect(Collectors.toList());

            PaymentData paymentData = PaymentData.builder()
                    .orderCode(orderCode)
                    .description(description)
                    .amount(totalPrice) // Sử dụng totalPrice
                    .returnUrl(returnUrl)
                    .cancelUrl(cancelUrl)
                    .items(payItems)
                    .build();

            CheckoutResponseData checkoutData = payOS.createPaymentLink(paymentData);

            response.put("orderCode", orderCode);
            response.put("paymentUrl", checkoutData.getCheckoutUrl());
            response.put("qr", checkoutData.getQrCode());
            response.put("message", "Tạo link thanh toán thành công");
        } catch (Exception e) {
            response.put("message", "Lỗi khi tạo link thanh toán: " + e.getMessage());
        }

        return response;
    }

    private ItemData mapOrderItemToItemData(OrderItem item) {
        String name = item.getProduct() != null ? item.getProduct().getName() :
                item.getPet() != null ? item.getPet().getName() : "Unknown";
        return ItemData.builder()
                .name(name)
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .build();
    }

    private long generateOrderCode() {
        String currentTimeString = String.valueOf(String.valueOf(new Date().getTime()));
        return Long.parseLong(currentTimeString.substring(currentTimeString.length() - 6));
    }

    private String generatePaymentDescription() {
        return generateRandomString(DEFAULT_DESCRIPTION_LENGTH);
    }

    private String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    private boolean isOrderValid(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            if (item.getProduct() != null) {
                Product product = item.getProduct();
                if (product.getStockQuantity() < item.getQuantity()) {
                    return false; // Không đủ số lượng sản phẩm
                }
            } else if (item.getPet() != null) {
                Pet pet = item.getPet();
                if (!pet.getStatus()) {
                    return false; // Thú cưng không còn khả dụng
                }
            }
        }
        return true; // Tất cả các mặt hàng đều hợp lệ
    }

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    public void saveOrderWithItems(Order order, List<OrderItem> items) {
        orderRepository.save(order);
        items.forEach(orderItemRepository::save);
    }

    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }
}
