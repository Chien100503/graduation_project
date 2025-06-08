package com.petshop.petopia.service.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.petshop.petopia.component.Global;
import com.petshop.petopia.dto.request.order.CreateOrderRequest;
import com.petshop.petopia.implement.PayosImpl;
import com.petshop.petopia.model.cart.CartItem;
import com.petshop.petopia.model.order.*;
import com.petshop.petopia.model.pet.Pet;
import com.petshop.petopia.model.product.Product;
import com.petshop.petopia.model.sale.Banner;
import com.petshop.petopia.model.user.Address;
import com.petshop.petopia.model.user.User;

import com.petshop.petopia.repository.cart.CartItemRepository;
import com.petshop.petopia.repository.order.OrderItemRepository;
import com.petshop.petopia.repository.order.OrderRepository;
import com.petshop.petopia.repository.order.PaymentRepository;
import com.petshop.petopia.repository.pet.PetRepository;
import com.petshop.petopia.repository.product.ProductRepository;
import com.petshop.petopia.repository.user.AddressRepository;
import com.petshop.petopia.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import vn.payos.type.PaymentLinkData;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AddressRepository addressRepository;
    private final PetRepository petRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final PayOS payOS;
    private final PayosImpl payOSImpl;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final AtomicLong orderCodeCounter = new AtomicLong(new Date().getTime() / 1000);
    private static final int DEFAULT_DESCRIPTION_LENGTH = 8;


    @Transactional
    public ObjectNode createOrder(CreateOrderRequest request, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại."));

        if (request.getAddressId() == null) {
            throw new IllegalArgumentException("Vui lòng cung cấp ID địa chỉ giao hàng.");
        }
        Address shippingAddressDetails = addressRepository.findByIdAndUserId(request.getAddressId(), userId)
                .orElseThrow(() -> new RuntimeException("Địa chỉ giao hàng không hợp lệ hoặc không thuộc về người dùng này."));

        List<OrderItem> orderItems = processCartItemsAndPrepareOrderItems(userId);

        isOrderValid(orderItems);

        BigDecimal finalTotalPrice = calculateTotalPriceFromOrderItems(orderItems);

        Order order = new Order();
        order.setUser(user);
        order.setRecipientName(shippingAddressDetails.getRecipientName());
        order.setShippingAddress(shippingAddressDetails.getFullAddress());
        order.setPhoneNumber(shippingAddressDetails.getPhone());
        order.setTotalPrice(finalTotalPrice);
        order.setStatus(Global.OrderStatus.PENDING);
        order = orderRepository.save(order);

        updateInventoryAndSaveOrderItems(order, orderItems);

        cartItemRepository.deleteCartItemsByUserId(userId);


        ObjectNode response;
        if (Global.PaymentMethod.PAYOS.name().equalsIgnoreCase(request.getPaymentMethod().name())) {
            response = handlePayOSPayment(order, finalTotalPrice);
        } else if (Global.PaymentMethod.COD.name().equalsIgnoreCase(request.getPaymentMethod().name())) {
            response = handleCodPayment(order);
        } else {
            throw new RuntimeException("Phương thức thanh toán không hợp lệ hoặc chưa được hỗ trợ.");
        }

        return response;
    }

    public PaymentLinkData getPaymentDetails(Long orderCode, Integer userId) {
        Payment payment = paymentRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin thanh toán với mã: " + orderCode));

        Order order = payment.getOrder();
        if (order == null || !order.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Bạn không có quyền truy cập thông tin thanh toán này.");
        }
        try {
            return payOS.getPaymentLinkInformation(orderCode);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy thông tin chi tiết đơn hàng từ PayOS: " + e.getMessage(), e);
        }
    }

    private List<OrderItem> processCartItemsAndPrepareOrderItems(Integer userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng của bạn đang trống. Không thể tạo đơn hàng.");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();

            BigDecimal originalPrice;
            Banner itemBanner = null;

            if (cartItem.getProduct() != null) {
                Product product = productRepository.findById(cartItem.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException("Sản phẩm trong giỏ hàng không tồn tại: " + cartItem.getProduct().getId()));
                orderItem.setProduct(product);
                originalPrice = product.getPrice();
                itemBanner = product.getBanner();
            } else if (cartItem.getPet() != null) {
                Pet pet = petRepository.findById(cartItem.getPet().getId())
                        .orElseThrow(() -> new RuntimeException("Thú cưng trong giỏ hàng không tồn tại: " + cartItem.getPet().getId()));
                orderItem.setPet(pet);
                originalPrice = pet.getPrice();
                itemBanner = pet.getBanner();
            } else {
                throw new RuntimeException("Mục trong giỏ hàng không hợp lệ (thiếu sản phẩm hoặc thú cưng).");
            }

            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(originalPrice);

            BigDecimal priceAfterBannerDiscount = originalPrice;
            if (itemBanner != null && itemBanner.getSalePercent() != null) {
                if (itemBanner.getIsActive()) {
                    BigDecimal discountFactor = BigDecimal.ONE.subtract(itemBanner.getSalePercent());
                    priceAfterBannerDiscount = originalPrice.multiply(discountFactor);
                }
            }
            orderItem.setPriceDiscount(priceAfterBannerDiscount.max(BigDecimal.ZERO));

            orderItems.add(orderItem);
        }
        return orderItems;
    }

    private void isOrderValid(List<OrderItem> orderItems) {
        if (orderItems.isEmpty()) {
            throw new RuntimeException("Đơn hàng không có sản phẩm/thú cưng nào.");
        }
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
                    throw new RuntimeException("Thú cưng '" + pet.getName() + "' hiện không có sẵn để bán.");
                }
                if (item.getQuantity() != 1) {
                    throw new RuntimeException("Số lượng thú cưng phải là 1.");
                }
            }
        }
    }

    private BigDecimal calculateTotalPriceFromOrderItems(List<OrderItem> orderItems) {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : orderItems) {
            if (item.getPriceDiscount() == null || item.getQuantity() == null) {
                throw new RuntimeException("Thông tin giá đã giảm hoặc số lượng của một sản phẩm/thú cưng trong đơn hàng không hợp lệ.");
            }
            total = total.add(item.getPriceDiscount().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        return total;
    }

    @Transactional
    public ObjectNode handlePayOSPayment(Order order, BigDecimal finalTotalPrice) {
        ObjectNode response = objectMapper.createObjectNode();
        Payment payment = null;

        try {
            long payosOrderCode = orderCodeCounter.incrementAndGet();
            String description = generateRandomString();

            List<ItemData> payosItems = order.getItems().stream()
                    .map(item -> ItemData.builder()
                            .name(item.getProduct() != null ? item.getProduct().getName() : item.getPet().getName())
                            .quantity(item.getQuantity())
                            .price(item.getPriceDiscount().intValue())
                            .build())
                    .collect(Collectors.toList());

            PaymentData paymentData = PaymentData.builder()
                    .orderCode(payosOrderCode)
                    .description(description)
                    .amount(finalTotalPrice.intValue())
                    .items(payosItems)
                    .returnUrl("")
                    .cancelUrl("")
                    .expiredAt(payOSImpl.calculateExpiredTime())
                    .build();

            CheckoutResponseData data = payOS.createPaymentLink(paymentData);

            payment = new Payment();
            payment.setPaymentMethod(Global.PaymentMethod.PAYOS);
            payment.setOrderCode(payosOrderCode);
            payment.setTransactionId(data.getPaymentLinkId());
            payment.setTransactionContent(description);
            payment.setOrder(order);

            payment = paymentRepository.save(payment);

            order.setPayment(payment);
            orderRepository.save(order);

            response.put("error", 0);
            response.put("message", "Tạo yêu cầu thanh toán PayOS thành công.");
            response.put("orderCode", payosOrderCode);
            response.put("qrCode", data.getQrCode());

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi khi khởi tạo thanh toán PayOS cho Đơn hàng ID " + (order != null ? order.getId() : "N/A") + ": " + e.getMessage());

            if (order != null) {
                order.setStatus(Global.OrderStatus.FAILED);
                orderRepository.save(order);
            }
            throw new RuntimeException("Lỗi khi xử lý yêu cầu thanh toán PayOS: " + e.getMessage(), e);
        }
    }

    private ObjectNode handleCodPayment(Order order) {
        ObjectNode response = objectMapper.createObjectNode();

        Payment payment = new Payment();
        payment.setPaymentMethod(Global.PaymentMethod.COD);
        payment.setTransactionContent("COD-" + UUID.randomUUID().toString().substring(0, 8));
        payment.setOrder(order);

        payment = paymentRepository.save(payment);

        order.setPayment(payment);
        order.setStatus(Global.OrderStatus.CONFIRMED);
        orderRepository.save(order);

        response.put("error", 0);
        response.put("message", "Đơn hàng COD đã được tạo thành công.");
        return response;
    }

    private void updateInventoryAndSaveOrderItems(Order order, List<OrderItem> orderItems) {
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(order);
            orderItemRepository.save(orderItem);

            if (orderItem.getProduct() != null) {
                Product product = productRepository.findById(orderItem.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại khi cập nhật tồn kho: " + orderItem.getProduct().getId()));
                if (product.getStockQuantity() < orderItem.getQuantity()) {
                    throw new RuntimeException("Sản phẩm '" + product.getName() + "' không đủ số lượng tồn kho để cập nhật.");
                }
                product.setStockQuantity(product.getStockQuantity() - orderItem.getQuantity());
                productRepository.save(product);
            } else if (orderItem.getPet() != null) {
                Pet pet = petRepository.findById(orderItem.getPet().getId())
                        .orElseThrow(() -> new RuntimeException("Thú cưng không tồn tại khi cập nhật trạng thái: " + orderItem.getPet().getId()));
                if (!pet.getStatus()) {
                    throw new RuntimeException("Thú cưng '" + pet.getName() + "' hiện không có sẵn để bán.");
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
        Payment payment = paymentRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin thanh toán với mã: " + orderCode));

        Order order = payment.getOrder();
        if (order == null) {
            throw new RuntimeException("Không tìm thấy đơn hàng liên kết với thanh toán này.");
        }
        if (!order.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Bạn không có quyền hủy yêu cầu thanh toán này.");
        }

        try {
            PaymentLinkData cancelledPayosOrder = payOS.cancelPaymentLink(orderCode, null);

            order.setStatus(Global.OrderStatus.CANCELLED);
            orderRepository.save(order);

            return cancelledPayosOrder;
        } catch (Exception e) {
            System.err.println("Lỗi khi hủy link thanh toán PayOS cho orderCode " + orderCode + ": " + e.getMessage());
            throw new RuntimeException("Lỗi khi hủy yêu cầu thanh toán PayOS với mã " + orderCode + ": " + e.getMessage(), e);
        }
    }

    private List<OrderItem> getOrderItemsForReorder(Integer orderId, Integer userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng."));
        if (!order.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Bạn không có quyền truy cập đơn hàng này.");
        }
        return order.getItems();
    }
}