package com.petshop.petopia.controller;

import com.petshop.petopia.dto.request.cart.CartItemRequest;
import com.petshop.petopia.dto.request.cart.CartItemUpdateRequest;
import com.petshop.petopia.dto.response.cart.CartResponse;
import com.petshop.petopia.security.JwtService;
import com.petshop.petopia.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final JwtService jwtService;

    @PostMapping(value = "/add", consumes = {"multipart/form-data"})
    public ResponseEntity<CartResponse> addToCart(
            @RequestHeader("Authorization") String token,
            @ModelAttribute CartItemRequest request) {
        Integer userId = jwtService.extractUserId(token);
        CartResponse updatedCart = cartService.addToCart(userId, request);
        return ResponseEntity.ok(updatedCart);
    }

    @PutMapping(value = "/update", consumes = {"multipart/form-data"})
    public ResponseEntity<CartResponse> updateCartItem(
            @ModelAttribute CartItemUpdateRequest request,
            @RequestHeader("Authorization") String token) {
        Integer userId = jwtService.extractUserId(token);
        CartResponse updatedCart = cartService.updateCartItem(userId, request);
        return ResponseEntity.ok(updatedCart);
    }

    // 📦 Xem giỏ hàng
    @GetMapping
    public ResponseEntity<CartResponse> getMyCart(@RequestHeader("Authorization") String token) {
        Integer userId = jwtService.extractUserId(token);
        CartResponse cartResponse = cartService.getCart(userId);
        return ResponseEntity.ok(cartResponse);
    }
}