package com.petshop.petopia.component;

import com.petshop.petopia.dto.response.review.ReviewResponse;
import com.petshop.petopia.model.review.Review;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ConvertReview {
    public ReviewResponse convertToResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setUserId(review.getUser().getId());
        response.setProductId(review.getProduct().getId());
        response.setComment(review.getComment());
        return response;
    }
}
