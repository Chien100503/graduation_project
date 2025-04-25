//package com.petshop.petopia.implement;
//
//import com.petshop.petopia.dto.request.order.OrderItemRequest;
//import com.petshop.petopia.dto.request.order.OrderRequest;
//import com.petshop.petopia.model.*;
//import com.petshop.petopia.model.order.Order;
//import com.petshop.petopia.model.order.OrderItem;
//import com.petshop.petopia.model.product.Pet;
//import com.petshop.petopia.model.product.Product;
//import com.petshop.petopia.repository.order.OrderRepository;
//import com.petshop.petopia.repository.product.PetRepository;
//import com.petshop.petopia.repository.product.ProductRepository;
//import com.petshop.petopia.repository.user.UserRepository;
//import com.petshop.petopia.service.OrderService;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//
//@Service
//@RequiredArgsConstructor
//public class OrderServiceImpl implements OrderService {
//
//    private final UserRepository userRepository;
//    private final ProductRepository productRepository;
//    private final PetRepository petRepository;
//    private final OrderRepository orderRepository;
//
//    @Transactional
//    @Override
//    public void placeOrder(OrderRequest request) {
//        User user = userRepository.findById(request.getUserId())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        Order order = new Order();
//        order.setUser(user);
//        order.setShippingAddress(request.getShippingAddress());
//        order.setPaymentMethod(request.getPaymentMethod());
//        order.setStatus("PENDING");
//        order.setCreatedAt(new Date());
//        order.setUpdatedAt(new Date());
//
//        List<OrderItem> orderItems = new ArrayList<>();
//        double total = 0;
//
//        for (OrderItemRequest itemReq : request.getItems()) {
//            OrderItem item = new OrderItem();
//
//            if (itemReq.getProductId() != null) {
//                Product product = productRepository.findById(itemReq.getProductId())
//                        .orElseThrow(() -> new RuntimeException("Product not found"));
//                item.setProduct(product);
//            }
//
//            if (itemReq.getPetId() != null) {
//                Pet pet = petRepository.findById(itemReq.getPetId())
//                        .orElseThrow(() -> new RuntimeException("Pet not found"));
//                item.setPet(pet);
//            }
//
//            item.setQuantity(itemReq.getQuantity());
//            item.setPrice(itemReq.getPrice());
//            item.setOrder(order);
//
//            total += item.getPrice() * item.getQuantity();
//            orderItems.add(item);
//        }
//
//        order.setTotalPrice(total);
//        order.setItems(orderItems);
//
//        orderRepository.save(order);
//    }
//}