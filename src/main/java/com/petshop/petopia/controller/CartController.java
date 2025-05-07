package com.petshop.petopia.controller;

import com.petshop.petopia.dto.request.cart.CartItemRequest;
import com.petshop.petopia.dto.request.cart.CartItemUpdateRequest;
import com.petshop.petopia.dto.response.cart.CartResponse;
import com.petshop.petopia.security.JwtService;
import com.petshop.petopia.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class  CartController {

    private final CartService cartService;
    private final JwtService jwtService;

    @PostMapping(value = "/add", consumes = {"multipart/form-data"})
    public ResponseEntity<CartResponse> addToCart(
            @RequestHeader("Authorization") String token,
            @ModelAttribute CartItemRequest request) {
        Integer userId = jwtService.extractUserId(token);
        CartResponse addToCart = cartService.addToCart(userId, request);
        return ResponseEntity.ok(addToCart);
    }

    @PutMapping(value = "/update", consumes = {"multipart/form-data", "application/json"})
    public ResponseEntity<CartResponse> updateCartItem(
            @ModelAttribute CartItemUpdateRequest request,
            @RequestHeader("Authorization") String token) {
        Integer userId = jwtService.extractUserId(token);
        CartResponse updatedCart = cartService.updateCartItem(userId, request);
        return ResponseEntity.ok(updatedCart);
    }

    @GetMapping
    public ResponseEntity<CartResponse> getMyCart(@RequestHeader("Authorization") String token) {
        Integer userId = jwtService.extractUserId(token);
        CartResponse cartResponse = cartService.getCart(userId);
        return ResponseEntity.ok(cartResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage()); // 403 Forbidden
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.internalServerError().body("Lỗi hệ thống không xác định: " + ex.getMessage()); // 500 Internal Server Error
    }
}