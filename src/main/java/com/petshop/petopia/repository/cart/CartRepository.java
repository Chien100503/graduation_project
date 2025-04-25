package com.petshop.petopia.repository.cart;

import com.petshop.petopia.model.User;
import com.petshop.petopia.model.cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByUser(User user);
    void deleteByUser(User user);
}