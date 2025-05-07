package com.petshop.petopia.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.petshop.petopia.config.RedisConfig;
import com.petshop.petopia.dto.request.order.CreateOrderRequest; // Import lớp request của bạn
import com.petshop.petopia.dto.request.order.OrderItemRequest; // Import lớp OrderItemRequest của bạn
import com.petshop.petopia.dto.request.order.OrderPendingInfo;
import com.petshop.petopia.implement.PayosImpl; // Import PayosImpl của bạn
import com.petshop.petopia.model.cart.CartItem;
import com.petshop.petopia.model.order.*; // Import các lớp model order của bạn
import com.petshop.petopia.model.pet.Pet; // Import lớp Pet của bạn
import com.petshop.petopia.model.product.Product; // Import lớp Product của bạn
import com.petshop.petopia.model.user.User; // Import lớp User của bạn
import com.petshop.petopia.repository.cart.CartItemRepository; // Import CartItemRepository của bạn
import com.petshop.petopia.repository.order.OrderItemRepository; // Import OrderItemRepository của bạn
import com.petshop.petopia.repository.order.OrderRepository; // Import OrderRepository của bạn
import com.petshop.petopia.repository.order.PaymentRepository; // Import PaymentRepository của bạn
import com.petshop.petopia.repository.pet.PetRepository; // Import PetRepository của bạn
import com.petshop.petopia.repository.product.ProductRepository; // Import ProductRepository của bạn
import com.petshop.petopia.repository.user.UserRepository; // Import UserRepository của bạn
import com.petshop.petopia.model.order.PaymentMethod; // Import enum PaymentMethod của bạn
import lombok.RequiredArgsConstructor; // Import Lombok RequiredArgsConstructor
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.AccessDeniedException; // Import nếu dùng exception này
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import cho @Transactional
// Import các lớp từ thư viện PayOS
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import vn.payos.type.PaymentLinkData;
import vn.payos.type.WebhookData;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong; // Sử dụng AtomicLong để gen orderCode an toàn hơn
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Tạo constructor với các final fields
public class OrderService {

    // Inject các Repository và PayOS (sử dụng Lombok @RequiredArgsConstructor)
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PetRepository petRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository; // Inject PaymentRepository
    private final PayOS payOS;
    private final PayosImpl payOSImpl; // Inject PayosImpl
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final AtomicLong orderCodeCounter = new AtomicLong(new Date().getTime() / 1000);
    private static final int DEFAULT_DESCRIPTION_LENGTH = 8; // Giữ nguyên hằng số này


    @Transactional
    public ObjectNode createOrder(CreateOrderRequest request, Integer userId) {
        // 1. Tìm người dùng
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<OrderItem> orderItems = new ArrayList<>();
        String returnUrl = "";
        String cancelUrl = "";
        boolean isFromCart = false;

        // 2. Xác định nguồn gốc đơn hàng và xử lý items dựa vào sự hiện diện của request.getItems()
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            // Đây là mua trực tiếp
            if (request.getItems().size() > 1) {
                // Kiểm tra giới hạn mua trực tiếp chỉ 1 loại item
                throw new RuntimeException("Mua trực tiếp chỉ cho phép mua một loại sản phẩm/thú cưng mỗi lần.");
            }
            // Xử lý item mua trực tiếp
            orderItems = processDirectOrderItems(request.getItems());
            isFromCart = false;
        } else {
            // Đây là mua từ giỏ hàng (items không được gửi lên hoặc rỗng)
            orderItems = processCartOrderItems(userId);
            isFromCart = true;
        }

        // 3. Kiểm tra tính khả thi của đơn hàng (số lượng tồn kho, trạng thái thú cưng)
        // isOrderValid sẽ ném RuntimeException nếu không hợp lệ
        isOrderValid(orderItems);


        // 4. Tính tổng giá trị đơn hàng
        Integer totalPrice = calculateTotalPrice(orderItems);

        // 5. Xử lý thanh toán PayOS trước khi lưu đơn hàng vào DB nội bộ
        if (PaymentMethod.PAYOS.name().equalsIgnoreCase(request.getPaymentMethod().name())) {
            // Đối với PayOS, chúng ta tạo yêu cầu thanh toán VÀ lưu đơn hàng nội bộ với trạng thái PENDING ngay tại đây.
            return handlePayOSPayment(user, orderItems, totalPrice, request.getShippingAddress(), request.getPhoneNumber(), returnUrl, cancelUrl);
        }

        // 6. Nếu không phải PayOS, tiến hành tạo và lưu đơn hàng vào DB nội bộ (áp dụng cho COD và các phương thức khác không cần API ngoài lúc tạo)
        Order order = new Order();
        Payment payment = new Payment();
        order.setUser(user);
        order.setShippingAddress(request.getShippingAddress());
        order.setPhoneNumber(request.getPhoneNumber());
        order.setOrderDate(new Date());
        order.setPaid(false); // COD ban đầu chưa thanh toán
        order.setDelivered(false);
        order.setReceived(false);
        payment.setPaymentMethod(request.getPaymentMethod());
        order.setTotalPrice(totalPrice);
        payment.setOrderCode(null);

        // Lưu đơn hàng chính trước để có ID
        order = orderRepository.save(order); // Lưu và nhận lại đối tượng Order đã có ID

        // 7. Lưu các mục đơn hàng (OrderItem) và cập nhật số lượng/trạng thái trong kho
        updateInventoryAndSaveOrderItems(order, orderItems);


        // 8. Nếu đơn hàng từ giỏ hàng, xóa các mục trong giỏ hàng
        if (isFromCart){
            cartItemRepository.deleteCartItemsByUserId(userId);
        }

        // 9. Xử lý thanh toán COD (hoặc các phương thức khác không cần gọi API ngoài lúc tạo đơn)
        if (PaymentMethod.COD.name().equalsIgnoreCase(request.getPaymentMethod().name())) {
            return handleCodPayment(order); // Trả về thông tin đơn hàng đã tạo
        } else {
            // Nếu đến đây mà không phải PayOS hay COD, có nghĩa là phương thức thanh toán không hợp lệ
            throw new RuntimeException("Phương thức thanh toán không hợp lệ hoặc chưa được xử lý.");
        }
    }

    public PaymentLinkData getOrderDetails(Long orderId, Integer userId) {
        // Logic kiểm tra quyền truy cập và gọi PayOS
        // Cần tìm đơn hàng nội bộ dựa trên orderId của PayOS VÀ userId
        Optional<Order> orderOptional = orderRepository.findByIdAndUser_Id(orderId, userId); // Giả sử có phương thức này

        if (!orderOptional.isPresent()) {
            // Nếu không tìm thấy đơn hàng nội bộ với orderId VÀ thuộc về userId này
            throw new AccessDeniedException("Order not found or you do not have access to this order.");
        }

        // Nếu đơn hàng nội bộ tồn tại và thuộc về người dùng, gọi PayOS để lấy thông tin chi tiết nhất
        try {
            PaymentLinkData payosOrderData = payOS.getPaymentLinkInformation(orderId);
            return payosOrderData;
        } catch (Exception e) {
            // Xử lý hoặc ném lại ngoại lệ nếu có lỗi khi gọi PayOS
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
            // Copy thông tin từ CartItem sang OrderItem
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setPet(cartItem.getPet());
            orderItem.setQuantity(cartItem.getQuantity());
            // Lấy giá hiện tại từ Product/Pet để đảm bảo giá chính xác tại thời điểm đặt hàng
            if (orderItem.getProduct() != null) {
                Product product = productRepository.findById(orderItem.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException("Sản phẩm trong giỏ hàng không tồn tại: " + orderItem.getProduct().getId()));
                orderItem.setPrice(product.getPrice());
            } else if (orderItem.getPet() != null) {
                Pet pet = petRepository.findById(orderItem.getPet().getId())
                        .orElseThrow(() -> new RuntimeException("Thú cưng trong giỏ hàng không tồn tại: " + orderItem.getPet().getId()));
                orderItem.setPrice(pet.getPrice());
            } else {
                // Xử lý trường hợp CartItem không có product hoặc pet (lỗi dữ liệu)
                throw new RuntimeException("Mục trong giỏ hàng không hợp lệ.");
            }
            orderItems.add(orderItem);
        }
        return orderItems;
    }

    private List<OrderItem> processDirectOrderItems(List<OrderItemRequest> requestItems) {
        List<OrderItem> orderItems = new ArrayList<>();
        // Do đã check size = 1 ở trên, nên chỉ cần xử lý item đầu tiên
        OrderItemRequest requestItem = requestItems.get(0);

        OrderItem orderItem = new OrderItem();
        // orderItem.setOrder(order); // Order sẽ được set sau khi lưu order chính

        if (requestItem.getProductId() != null) { // Sử dụng ProductId từ OrderItemRequest
            // Tìm Product theo ID từ request
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
            orderItem.setQuantity(1); // Mua thú cưng thường số lượng là 1
            orderItem.setPrice(pet.getPrice()); // Lấy giá từ Pet hiện tại
        } else {
            throw new RuntimeException("Thông tin sản phẩm hoặc thú cưng không hợp lệ cho mua trực tiếp.");
        }

        // Kiểm tra số lượng > 0
        if (orderItem.getQuantity() <= 0) {
            throw new RuntimeException("Số lượng sản phẩm/thú cưng phải lớn hơn 0.");
        }

        orderItems.add(orderItem);
        return orderItems;
    }

    private boolean isOrderValid(List<OrderItem> orderItems) {
        // Duyệt qua từng OrderItem và kiểm tra
        for (OrderItem item : orderItems) {
            // Kiểm tra xem OrderItem có product hoặc pet không
            if (item.getProduct() == null && item.getPet() == null) {
                throw new RuntimeException("Mục đơn hàng không hợp lệ: Thiếu thông tin sản phẩm hoặc thú cưng.");
            }

            if (item.getProduct() != null) {
                // Kiểm tra số lượng tồn kho của sản phẩm
                Product product = productRepository.findById(item.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại: " + item.getProduct().getId()));
                if (product.getStockQuantity() < item.getQuantity()) {
                    // Ném exception
                    throw new RuntimeException("Sản phẩm '" + product.getName() + "' không đủ số lượng tồn kho.");
                }
            } else if (item.getPet() != null) {
                // Kiểm tra trạng thái của thú cưng
                Pet pet = petRepository.findById(item.getPet().getId())
                        .orElseThrow(() -> new RuntimeException("Thú cưng không tồn tại: " + item.getPet().getId()));
                if (!pet.getStatus()) {
                    // Ném exception
                    throw new RuntimeException("Thú cưng '" + pet.getName() + "' không có sẵn.");
                }
                // Mua thú cưng số lượng luôn là 1
                if (item.getQuantity() != 1) {
                    throw new RuntimeException("Số lượng thú cưng phải là 1.");
                }
            }
        }
        return true; // Nếu tất cả đều hợp lệ
    }

    private Integer calculateTotalPrice(List<OrderItem> orderItems) {
        Integer total = 0;
        for (OrderItem item : orderItems) {
            // Đảm bảo item.getPrice() và item.getQuantity() không null và là số hợp lệ
            if (item.getPrice() != null && item.getQuantity() != null) {
                total += item.getPrice() * item.getQuantity();
            } else {
                throw new RuntimeException("Thông tin giá hoặc số lượng sản phẩm/thú cưng không hợp lệ.");
            }
        }
        return total;
    }

    private ObjectNode handlePayOSPayment(User user, List<OrderItem> orderItems, Integer totalPrice, String shippingAddress, String phoneNumber, String returnUrl, String cancelUrl) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        try {
            long orderCode = orderCodeCounter.incrementAndGet();
            String description = generateRandomString(); // Gen description

            // Chuyển đổi OrderItem sang ItemData của PayOS
            List<ItemData> payosItems = orderItems.stream()
                    .map(item -> ItemData.builder()
                            .name(item.getProduct() != null ? item.getProduct().getName() : item.getPet().getName()) // Lấy tên sản phẩm/thú cưng
                            .quantity(item.getQuantity())
                            .price(item.getPrice())
                            .build())
                    .collect(Collectors.toList());

            // Tạo PaymentData cho PayOS
            PaymentData paymentData = PaymentData.builder()
                    .orderCode(orderCode)
                    .description(description)
                    .amount(totalPrice)
                    .items(payosItems)
                    .returnUrl(returnUrl)
                    .cancelUrl(cancelUrl)
                    .expiredAt(payOSImpl.calculateExpiredTime())
                    .build();

            CheckoutResponseData data = payOS.createPaymentLink(paymentData);

            OrderPendingInfo pendingInfo = new OrderPendingInfo(String.valueOf(orderCode), user.getId());
            redisTemplate.opsForValue().set(String.valueOf(orderCode), pendingInfo, Duration.ofMinutes(30));

            // Xây dựng phản hồi thành công cho client
            response.put("error", 0);
            response.put("message", "success");
            response.set("data", objectMapper.valueToTree(data));
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error creating PayOS payment link: " + e.getMessage());
            throw new RuntimeException("Lỗi khi tạo yêu cầu thanh toán PayOS: " + e.getMessage(), e);
        }
    }

    private ObjectNode handleCodPayment(Order order) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        // Đối với COD, đơn hàng đã được lưu và trừ kho ở bước trên.
        // Tạo bản ghi Payment cho COD
        Payment payment = new Payment();
        payment.setPaymentMethod(PaymentMethod.COD);
        payment.setPaymentDate(new Date()); // Ngày tạo đơn coi như ngày yêu cầu thanh toán COD
        payment.setTransactionContent("COD-" + UUID.randomUUID()); // Gen transaction content tạm thời
        payment.setOrder(order); // Liên kết Payment với Order
        // Trạng thái thanh toán (paid) sẽ được cập nhật sau khi nhận hàng

        paymentRepository.save(payment); // Lưu bản ghi Payment

        // Cập nhật lại Order với thông tin Payment vừa tạo
        order.setPayment(payment);
        orderRepository.save(order);

        // Chỉ cần trả về thông tin đơn hàng đã lưu.
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
                // Đảm bảo số lượng tồn kho đủ trước khi trừ (đã check trong isOrderValid, nhưng kiểm tra lại cũng tốt)
                if (product.getStockQuantity() < orderItem.getQuantity()) {
                    // Ném exception - Transaction sẽ rollback
                    throw new RuntimeException("Sản phẩm '" + product.getName() + "' không đủ số lượng tồn kho khi cập nhật.");
                }
                product.setStockQuantity(product.getStockQuantity() - orderItem.getQuantity());
                productRepository.save(product); // Lưu cập nhật số lượng
            } else if (orderItem.getPet() != null) {
                Pet pet = orderItem.getPet();
                // Đảm bảo thú cưng đang có trạng thái true (có sẵn)
                if (!pet.getStatus()) {
                    // Ném exception - Transaction sẽ rollback
                    throw new RuntimeException("Thú cưng '" + pet.getName() + "' không có sẵn khi cập nhật.");
                }
                pet.setStatus(false); // Đánh dấu thú cưng đã được đặt
                petRepository.save(pet); // Lưu cập nhật trạng thái
            }
            // Nếu OrderItem không có cả product và pet, logic isOrderValid đã ném exception rồi.
        }
    }

    private void revertInventory(List<OrderItem> orderItems) {
        for (OrderItem orderItem : orderItems) {
            if (orderItem.getProduct() != null) {
                Product product = productRepository.findById(orderItem.getProduct().getId())
                        .orElse(null); // Sử dụng orElse(null) hoặc xử lý khác nếu sản phẩm không còn tồn tại

                if (product != null) {
                    product.setStockQuantity(product.getStockQuantity() + orderItem.getQuantity());
                    productRepository.save(product);
                }
            } else if (orderItem.getPet() != null) {
                Pet pet = petRepository.findById(orderItem.getPet().getId())
                        .orElse(null); // Sử dụng orElse(null) hoặc xử lý khác nếu thú cưng không còn tồn tại

                if (pet != null) {
                    pet.setStatus(true); // Đánh dấu thú cưng có sẵn trở lại
                    petRepository.save(pet);
                }
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
     public PaymentLinkData cancelOrder(Long orderId, Integer userId) {
          // Logic kiểm tra quyền
          Optional<Order> orderOptional = orderRepository.findByIdAndUser_Id(orderId, userId);
          if (!orderOptional.isPresent()) {
              throw new AccessDeniedException("Order not found or you do not have access.");
          }
          Order order = orderOptional.get();

          // Kiểm tra trạng thái đơn hàng (chỉ hủy khi chưa thanh toán)
          if (order.isPaid()) {
              throw new RuntimeException("Cannot cancel a paid order.");
          }

          try {
              // Gọi PayOS để hủy liên kết thanh toán
              PaymentLinkData cancelledPayosOrder = payOS.cancelPaymentLink(orderId, null); // Tham số thứ 2 là lý do hủy (String)

              // Cập nhật trạng thái đơn hàng nội bộ thành CANCELLED
              // order.setStatus(OrderStatus.CANCELLED); // Cần thêm trường status
              order.setPaid(false); // Đảm bảo paid là false
              orderRepository.save(order);

              // Hoàn trả số lượng tồn kho/trạng thái thú cưng
              revertInventory(order.getItems());

              return cancelledPayosOrder;
          } catch (Exception e) {
              throw new RuntimeException("Error cancelling order with PayOS: " + e.getMessage(), e);
          }
     }

}
