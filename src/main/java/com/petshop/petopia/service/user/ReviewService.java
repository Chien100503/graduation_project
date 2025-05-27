package com.petshop.petopia.service.user;

import com.petshop.petopia.dto.request.review.ReviewRequest;
import com.petshop.petopia.dto.response.review.ReviewResponse;
import com.petshop.petopia.model.review.Review;
import com.petshop.petopia.model.product.Product;
import com.petshop.petopia.model.user.User;
import com.petshop.petopia.repository.product.ProductRepository;
import com.petshop.petopia.repository.product.ReviewRepository;
import com.petshop.petopia.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public List<ReviewResponse> getReviewsByProduct(Integer productId) {
        return reviewRepository.findByProductId(productId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewResponse createReview(Integer userId, Integer productId, ReviewRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + userId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + productId));

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setComment(request.getComment());
        Review savedReview = reviewRepository.save(review);
        return convertToResponse(savedReview);
    }

    private ReviewResponse convertToResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setUserId(review.getUser().getId());
        response.setProductId(review.getProduct().getId());
        response.setComment(review.getComment());
        return response;
    }
}

