package com.petshop.petopia.controller;

import com.petshop.petopia.security.JwtService;
import com.petshop.petopia.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/{productId}/ratings")
    public ResponseEntity<String> submitRating(
            @PathVariable("productId") Integer productId, // Changed to Integer
            @RequestHeader("Authorization") String token,
            @RequestParam Integer rating) {
        Integer userId = jwtService.extractUserId(token);
        try {
            reviewService.submitProductRating(productId, userId, rating);
            return ResponseEntity.ok("Rating submitted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{productId}/reviews")
    public ResponseEntity<String> submitReview(
            @PathVariable("productId") Integer productId, // Changed to Integer
            @RequestHeader("Authorization") String token,
            @RequestParam String comment) {
        Integer userId = jwtService.extractUserId(token);
        try {
            reviewService.submitProductComment(productId, userId, comment);
            return ResponseEntity.ok("Review submitted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{productId}/ratings")  // Endpoint to get rating data
    public ResponseEntity<Map<String, Object>> getProductRating(
            @PathVariable("productId") Integer productId) { // Changed to Integer
        Map<Integer, Double> ratingPercentages = reviewService.calculateRatingPercentages(productId);
        Double averageRating = reviewService.calculateAverageRating(productId);
        Integer totalRatingPoints = reviewService.calculateTotalRatingPoints(productId);

        Map<String, Object> result = new HashMap<>();
        result.put("ratingPercentages", ratingPercentages);
        result.put("averageRating", averageRating);
        result.put("totalRatingPoints", totalRatingPoints);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{productId}/reviews")  // Endpoint to get reviews with pagination
    public ResponseEntity<Map<String, Object>> getProductReviews(
            @PathVariable("productId") Integer productId, // Changed to Integer
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> result = reviewService.getProductReviewsWithPagination(productId, page, size); // Call new method in service
        return ResponseEntity.ok(result);
    }
}

