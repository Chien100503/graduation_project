package com.petshop.petopia.repository.order;

import com.petshop.petopia.model.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserIdOrderByCreatedAtDesc(Integer userId);
    List<Order> findByUserId(Integer userId);
    Optional<Order> findByIdAndUser_Id(Long orderId, Integer userId);
    Optional<Order> findByPayment_TransactionContent(String transactionContent);
    List<Order> findByUser_Id(Integer userId);
}