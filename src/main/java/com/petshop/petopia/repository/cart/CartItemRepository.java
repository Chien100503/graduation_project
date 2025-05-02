package com.petshop.petopia.repository.cart;

import com.petshop.petopia.model.cart.Cart;
import com.petshop.petopia.model.cart.CartItem;
import com.petshop.petopia.model.pet.Pet;
import com.petshop.petopia.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    Optional<CartItem> findByCartAndPet(Cart cart, Pet pet);
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    List<CartItem> findByCart(Cart cart);
    Optional<CartItem> findByIdAndCart(Integer id, Cart cart);

    @Query("SELECT c FROM CartItem c WHERE c.cart.user.id = :userId")
    List<CartItem> findByUserId(Integer userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.cart.user.id = :userId")
    void deleteCartItemsByUserId(Integer userId);
}