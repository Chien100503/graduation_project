package com.petshop.petopia.controller.user;

import com.petshop.petopia.dto.request.review.RatingRequest;
import com.petshop.petopia.dto.response.review.RatingOverviewResponse;
import com.petshop.petopia.security.JwtService;
import com.petshop.petopia.service.user.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rating")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;
    private final JwtService jwtService;

    @GetMapping("/{productId}")
    public ResponseEntity<RatingOverviewResponse> getRatingOverview(
            @PathVariable Integer productId
    ) {
        RatingOverviewResponse ratingOverview = ratingService.getRatingOverview(productId);
        return ResponseEntity.ok(ratingOverview);
    }

    @PostMapping("/{productId}")
    public ResponseEntity<Void> rateProduct(
            @PathVariable Integer productId,
            @RequestBody RatingRequest request,
            @RequestHeader("Authorization") String token) {
        Integer userId = jwtService.extractUserId(token);
        ratingService.rateProduct(userId, productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
