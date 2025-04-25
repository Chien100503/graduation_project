package com.petshop.petopia.repository.order;

import com.petshop.petopia.model.User;
import com.petshop.petopia.model.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUser(User user);
}