package com.petshop.petopia.controller.user;

import com.petshop.petopia.dto.request.user.WishlistRequest;
import com.petshop.petopia.dto.response.user.WishlistItemResponse;
import com.petshop.petopia.security.JwtService;
import com.petshop.petopia.service.user.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final JwtService jwtService;
    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<List<WishlistItemResponse>> getUserWishlist(@RequestHeader("Authorization") String token) {
        Integer userId = jwtService.extractUserId(token);
        List<WishlistItemResponse> wishlist = wishlistService.getUserWishList(userId);
        return ResponseEntity.ok(wishlist);
    }

    @PostMapping
    public ResponseEntity<Void> addToWishlist(
            @RequestHeader("Authorization") String token,
            @RequestBody WishlistRequest request) {
        Integer userId = jwtService.extractUserId(token);

        if ((request.getPetId() == null && request.getProductId() == null) ||
                (request.getPetId() != null && request.getProductId() != null)) {
            return ResponseEntity.badRequest().build();
        }

        wishlistService.addToWishlist(userId, request.getPetId(), request.getProductId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> removeFromWishlist(
            @RequestHeader("Authorization") String token,
            @RequestBody WishlistRequest request) {
        Integer userId = jwtService.extractUserId(token);

        if ((request.getPetId() == null && request.getProductId() == null) ||
                (request.getPetId() != null && request.getProductId() != null)) {
            return ResponseEntity.badRequest().build();
        }

        wishlistService.removeFromWishlist(userId, request.getPetId(), request.getProductId());
        return ResponseEntity.ok().build();
    }
}
