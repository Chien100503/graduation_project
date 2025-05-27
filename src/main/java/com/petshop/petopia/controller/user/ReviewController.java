package com.petshop.petopia.controller.user;

import com.petshop.petopia.dto.request.review.ReviewRequest;
import com.petshop.petopia.dto.response.review.ReviewResponse;
import com.petshop.petopia.security.JwtService;
import com.petshop.petopia.service.user.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final JwtService jwtService;

    @GetMapping("/{productId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByProduct(@PathVariable Integer productId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByProduct(productId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/{productId}")
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Integer productId,
            @RequestBody ReviewRequest request,
            @RequestHeader("Authorization") String token) {
        Integer userId = jwtService.extractUserId(token);
        ReviewResponse createdReview = reviewService.createReview(userId, productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }


}

