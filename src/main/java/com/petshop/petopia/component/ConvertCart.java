package com.petshop.petopia.component;

import com.petshop.petopia.dto.response.cart.CartResponse;
import com.petshop.petopia.dto.response.cart.CartItemResponse;
import com.petshop.petopia.model.cart.Cart;
import com.petshop.petopia.model.cart.CartItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList; // Import ArrayList

@Component
public class ConvertCart {

    public CartResponse toCartResponse(Cart cart) {
        if (cart == null) {
            return null;
        }
        CartResponse cartResponse = new CartResponse();
        cartResponse.setId(cart.getId());
        List<CartItemResponse> cartItemResponses = (cart.getItems() != null) ?
                cart.getItems().stream()
                        .map(this::toCartItemResponse)
                        .collect(Collectors.toList()) : new ArrayList<>();
        cartResponse.setItems(cartItemResponses);
        cartResponse.setTotalPrice(cart.getTotalPrice()); // Sao chép totalPrice
        return cartResponse;
    }

    public CartItemResponse toCartItemResponse(CartItem cartItem) {
        CartItemResponse cartItemResponse = new CartItemResponse();
        cartItemResponse.setId(cartItem.getId());
        if (cartItem.getProduct() != null) {
            cartItemResponse.setProductId(cartItem.getProduct().getId());
            cartItemResponse.setProductName(cartItem.getProduct().getName());
        } else if (cartItem.getPet() != null) {
            cartItemResponse.setPetId(cartItem.getPet().getId());
            cartItemResponse.setPetName(cartItem.getPet().getName());
        }
        cartItemResponse.setQuantity(cartItem.getQuantity());
        cartItemResponse.setPrice(cartItem.getPrice());
        cartItemResponse.setItemTotalPrice(cartItem.getItemTotalPrice());
        return cartItemResponse;
    }
}