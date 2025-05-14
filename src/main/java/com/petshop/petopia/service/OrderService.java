package com.petshop.petopia.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.petshop.petopia.dto.request.order.CreateOrderRequest;
import com.petshop.petopia.dto.request.order.OrderItemRequest;
import com.petshop.petopia.dto.request.order.OrderPendingInfo;
import com.petshop.petopia.implement.PayosImpl;
import com.petshop.petopia.model.cart.CartItem;
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
import com.petshop.petopia.model.order.PaymentMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import vn.payos.type.PaymentLinkData;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong; // Sử dụng AtomicLong để gen orderCode an toàn hơn
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
    private final PaymentRepository paymentRepository; // Inject PaymentRepository
    private final PayOS payOS;
    private final PayosImpl payOSImpl;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final AtomicLong orderCodeCounter = new AtomicLong(new Date().getTime() / 1000);
    private static final int DEFAULT_DESCRIPTION_LENGTH = 8;


    @Transactional
    public ObjectNode createOrder(CreateOrderRequest request, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<OrderItem> orderItems;
        boolean isFromCart;

        if (request.getItems() != null && !request.getItems().isEmpty()) {
            orderItems = processDirectOrderItems(request.getItems());
            isFromCart = false;
        } else {
            orderItems = processCartOrderItems(userId);
            isFromCart = true;
        }

        isOrderValid(orderItems);

        Integer totalPrice = calculateTotalPrice(orderItems);

        Order order = createAndSaveOrder(user, request, totalPrice);


        if (isFromCart) {
            cartItemRepository.deleteCartItemsByUserId(userId);
        }

        if (PaymentMethod.PAYOS.name().equalsIgnoreCase(request.getPaymentMethod().name())) {
            return handlePayOSPayment(order, user, orderItems, totalPrice, request.getShippingAddress(), request.getPhoneNumber());

        } else if (PaymentMethod.COD.name().equalsIgnoreCase(request.getPaymentMethod().name())) {
            return handleCodPayment(order, orderItems);

        } else {
            throw new RuntimeException("Phương thức thanh toán không hợp lệ hoặc chưa được xử lý.");
        }
    }

    private Order createAndSaveOrder(User user, CreateOrderRequest request, Integer totalPrice) {
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(request.getShippingAddress());
        order.setPhoneNumber(request.getPhoneNumber());
        order.setOrderDate(new Date());
        order.setPaid(false);
        order.setDelivered(false);
        order.setReceived(false);
        order.setTotalPrice(totalPrice);
        order.setStatus(OrderStatus.PENDING);
        return orderRepository.save(order);
    }


    public PaymentLinkData getPaymentDetails(Long orderCode, Integer userId) {
        String redisKey = String.valueOf(orderCode);
        OrderPendingInfo pendingInfo = (OrderPendingInfo) redisTemplate.opsForValue().get(redisKey);

        if (pendingInfo == null) {
            throw new IllegalArgumentException("Không tìm thấy yêu cầu thanh toán đang chờ xử lý với mã: " + orderCode);
        }
        try {
            return payOS.getPaymentLinkInformation(orderCode);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching order details from PayOS", e);
        }
    }

    private List<OrderItem> processCartOrderItems(Integer userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId); // Giả sử có phương thức này

        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống.");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setPet(cartItem.getPet());
            orderItem.setQuantity(cartItem.getQuantity());
            if (orderItem.getProduct() != null) {
                Product product = productRepository.findById(orderItem.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException("Sản phẩm trong giỏ hàng không tồn tại: " + orderItem.getProduct().getId()));
                orderItem.setPrice(product.getPrice());
            } else if (orderItem.getPet() != null) {
                Pet pet = petRepository.findById(orderItem.getPet().getId())
                        .orElseThrow(() -> new RuntimeException("Thú cưng trong giỏ hàng không tồn tại: " + orderItem.getPet().getId()));
                orderItem.setPrice(pet.getPrice());
            } else {
                throw new RuntimeException("Mục trong giỏ hàng không hợp lệ.");
            }
            orderItems.add(orderItem);
        }
        return orderItems;
    }

    private List<OrderItem> processDirectOrderItems(List<OrderItemRequest> requestItems) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequest requestItem : requestItems) {
            OrderItem orderItem = new OrderItem();

            if (requestItem.getProductId() != null) {
                Product product = productRepository.findById(requestItem.getProductId())
                        .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại với ID: " + requestItem.getProductId()));
                orderItem.setProduct(product);
                orderItem.setQuantity(requestItem.getQuantity());
                orderItem.setPrice(product.getPrice()); // Lấy giá từ Product hiện tại
            } else if (requestItem.getPetId() != null) { // Sử dụng PetId từ OrderItemRequest
                // Tìm Pet theo ID từ request
                Pet pet = petRepository.findById(requestItem.getPetId())
                        .orElseThrow(() -> new RuntimeException("Thú cưng không tồn tại với ID: " + requestItem.getPetId()));
                orderItem.setPet(pet);
                orderItem.setQuantity(1);
                orderItem.setPrice(pet.getPrice());
            } else {
                throw new RuntimeException("Thông tin sản phẩm hoặc thú cưng không hợp lệ cho mua trực tiếp.");
            }

            if (orderItem.getQuantity() <= 0) {
                throw new RuntimeException("Số lượng sản phẩm/thú cưng phải lớn hơn 0.");
            }
            orderItems.add(orderItem);
        }
        return orderItems;
    }

    private void isOrderValid(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            if (item.getProduct() == null && item.getPet() == null) {
                throw new RuntimeException("Mục đơn hàng không hợp lệ: Thiếu thông tin sản phẩm hoặc thú cưng.");
            }

            if (item.getProduct() != null) {
                Product product = productRepository.findById(item.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại: " + item.getProduct().getId()));
                if (product.getStockQuantity() < item.getQuantity()) {
                    throw new RuntimeException("Sản phẩm '" + product.getName() + "' không đủ số lượng tồn kho.");
                }
            } else {
                Pet pet = petRepository.findById(item.getPet().getId())
                        .orElseThrow(() -> new RuntimeException("Thú cưng không tồn tại: " + item.getPet().getId()));
                if (!pet.getStatus()) {
                    throw new RuntimeException("Thú cưng '" + pet.getName() + "' không có sẵn.");
                }
                if (item.getQuantity() != 1) {
                    throw new RuntimeException("Số lượng thú cưng phải là 1.");
                }
            }
        }
    }

    private Integer calculateTotalPrice(List<OrderItem> orderItems) {
        int total = 0;
        for (OrderItem item : orderItems) {
            if (item.getPrice() != null && item.getQuantity() != null) {
                total += item.getPrice() * item.getQuantity();
            } else {
                throw new RuntimeException("Thông tin giá hoặc số lượng sản phẩm/thú cưng không hợp lệ.");
            }
        }
        return total;
    }

    private ObjectNode handlePayOSPayment(Order order, User user, List<OrderItem> orderItems, Integer totalPrice, String shippingAddress, String phoneNumber) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        try {
            long orderCode = order.getId(); // Sử dụng ID của order vừa tạo
            String description = generateRandomString();

            List<ItemData> payosItems = orderItems.stream()
                    .map(item -> ItemData.builder()
                            .name(item.getProduct() != null ? item.getProduct().getName() : item.getPet().getName())
                            .quantity(item.getQuantity())
                            .price(item.getPrice())
                            .build())
                    .collect(Collectors.toList());

            PaymentData paymentData = PaymentData.builder()
                    .orderCode(orderCode)
                    .description(description)
                    .amount(totalPrice)
                    .items(payosItems)
                    .returnUrl("")
                    .cancelUrl("")
                    .expiredAt(payOSImpl.calculateExpiredTime())
                    .build();

            CheckoutResponseData data = payOS.createPaymentLink(paymentData);

            OrderPendingInfo pendingInfo = new OrderPendingInfo(String.valueOf(orderCode), user.getId());
            redisTemplate.opsForValue().set(String.valueOf(orderCode), pendingInfo, Duration.ofMinutes(30));

            response.put("error", 0);
            response.put("message", "success");
            response.set("data", objectMapper.valueToTree(data));
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error creating PayOS payment link: " + e.getMessage());
            order.setStatus(OrderStatus.FAILED);
            orderRepository.save(order);
            throw new RuntimeException("Lỗi khi tạo yêu cầu thanh toán PayOS: " + e.getMessage(), e);
        }
    }

    private ObjectNode handleCodPayment(Order order, List<OrderItem> orderItems) {
        updateInventoryAndSaveOrderItems(order, orderItems);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        Payment payment = new Payment();
        payment.setPaymentMethod(PaymentMethod.COD);
        payment.setPaymentDate(new Date());
        payment.setTransactionContent("COD-" + UUID.randomUUID());
        payment.setOrder(order);
        order.setStatus(OrderStatus.CONFIRMED);
        paymentRepository.save(payment);
        order.setPayment(payment);
        orderRepository.save(order);

        response.put("error", 0); // Thêm trường error
        response.put("message", "Đơn hàng COD đã được tạo thành công.");
        response.set("data", objectMapper.valueToTree(order)); // Trả về thông tin đơn hàng nội bộ
        return response;
    }

    private void updateInventoryAndSaveOrderItems(Order order, List<OrderItem> orderItems) {
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(order); // Set order cho từng item
            orderItemRepository.save(orderItem); // Lưu OrderItem

            // Cập nhật số lượng/trạng thái trong kho
            if (orderItem.getProduct() != null) {
                Product product = orderItem.getProduct();
                if (product.getStockQuantity() < orderItem.getQuantity()) {
                    throw new RuntimeException("Sản phẩm '" + product.getName() + "' không đủ số lượng tồn kho khi cập nhật.");
                }
                product.setStockQuantity(product.getStockQuantity() - orderItem.getQuantity());
                productRepository.save(product);
            } else if (orderItem.getPet() != null) {
                Pet pet = orderItem.getPet();
                if (!pet.getStatus()) {
                    throw new RuntimeException("Thú cưng '" + pet.getName() + "' không có sẵn khi cập nhật.");
                }
                pet.setStatus(false);
                petRepository.save(pet);
            }
        }
    }

    private String generateRandomString() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(OrderService.DEFAULT_DESCRIPTION_LENGTH);
        for (int i = 0; i < OrderService.DEFAULT_DESCRIPTION_LENGTH; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    @Transactional
    public PaymentLinkData cancelOrder(Long orderCode, Integer userId) {
        String redisKey = String.valueOf(orderCode);

        OrderPendingInfo pendingInfo = (OrderPendingInfo) redisTemplate.opsForValue().get(redisKey);

        if (pendingInfo == null) {
            throw new IllegalArgumentException("Không tìm thấy yêu cầu thanh toán đang chờ xử lý với mã: " + orderCode);
        }

        if (!pendingInfo.getUserId().equals(userId)) {
            throw new AccessDeniedException("Bạn không có quyền hủy yêu cầu thanh toán này.");
        }

        try {
            PaymentLinkData cancelledPayosOrder = payOS.cancelPaymentLink(orderCode, null);

            redisTemplate.delete(redisKey);
            return cancelledPayosOrder;
        } catch (Exception e) {
            System.err.println("Error cancelling PayOS payment link for orderCode " + orderCode + ": " + e.getMessage());
            throw new RuntimeException("Lỗi khi hủy yêu cầu thanh toán PayOS với mã " + orderCode + ": " + e.getMessage(), e);
        }
    }

    private List<OrderItem> getOrderItemsForReorder(Integer orderId, Integer userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (!order.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to access this order.");
        }
        return order.getItems();
    }
}