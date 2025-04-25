package com.petshop.petopia.service;

import com.petshop.petopia.model.User;
import com.petshop.petopia.model.cart.Cart;
import com.petshop.petopia.model.cart.CartItem;
import com.petshop.petopia.model.order.Order;
import com.petshop.petopia.model.order.OrderItem;
import com.petshop.petopia.model.product.Pet;
import com.petshop.petopia.repository.*;
import com.petshop.petopia.repository.cart.CartRepository;
import com.petshop.petopia.repository.order.OrderItemRepository;
import com.petshop.petopia.repository.order.OrderRepository;
import com.petshop.petopia.repository.product.PetRepository;
import com.petshop.petopia.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;

    @Transactional
    public Order placeOrder(Integer userId, String shippingAddress, String phoneNumber) {
        User user = userRepository.findById(userId).orElseThrow();
        Cart cart = cartRepository.findByUser(user).orElseThrow();

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(new Date());
        order.setShippingAddress(shippingAddress);
        order.setPhoneNumber(phoneNumber);
        List<OrderItem> orderItems = new ArrayList<>();

        double total = 0.0;

        for (CartItem item : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(item.getProduct());
            orderItem.setPet(item.getPet());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(item.getPrice());

            if (item.getPet() != null) {
                Pet pet = item.getPet();
                pet.setStatus(false);
                petRepository.save(pet);
            }

            total += item.getPrice() * item.getQuantity();
            orderItems.add(orderItem);
        }

        order.setItems(orderItems);
        order.setTotalPrice(total);

        Order savedOrder = orderRepository.save(order);
        cartRepository.delete(cart);

        return savedOrder;
    }
}