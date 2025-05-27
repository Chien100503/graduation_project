package com.petshop.petopia.service.user;

import com.petshop.petopia.dto.request.review.RatingRequest;
import com.petshop.petopia.dto.response.review.RatingOverviewResponse;
import com.petshop.petopia.model.product.Product;
import com.petshop.petopia.model.review.ProductRating;
import com.petshop.petopia.model.review.Rating;
import com.petshop.petopia.model.user.User;
import com.petshop.petopia.repository.product.ProductRatingRepository;
import com.petshop.petopia.repository.product.ProductRepository;
import com.petshop.petopia.repository.product.RatingRepository;
import com.petshop.petopia.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;
    private final ProductRatingRepository productRatingRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void rateProduct(Integer userId, Integer productId, RatingRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));

        productRatingRepository.findByUserIdAndProductId(userId, productId)
                .ifPresentOrElse(existingRating -> updateRating(existingRating, request.getRatingValue(), product),
                        () -> createRating(user, product, request.getRatingValue())
                );
    }

    private void createRating(User user, Product product, Integer ratingValue) {
        ProductRating newRating = ProductRating.builder()
                .user(user)
                .product(product)
                .ratingValue(ratingValue)
                .build();
        productRatingRepository.save(newRating);
        updateRatingCount(product, ratingValue, 1);
    }

    private void updateRating(ProductRating existingRating, Integer newRatingValue, Product product) {
        Integer oldRatingValue = existingRating.getRatingValue();
        existingRating.setRatingValue(newRatingValue);
        productRatingRepository.save(existingRating);
        updateRatingCount(product, oldRatingValue, -1);
        updateRatingCount(product, newRatingValue, 1);
    }

    private void updateRatingCount(Product product, Integer ratingValue, int increment) {
        Rating rating = ratingRepository.findByProductId(product.getId())
                .orElseGet(() -> {
                    Rating newRating = new Rating();
                    newRating.setProduct(product);
                    return ratingRepository.save(newRating);
                });

        switch (ratingValue) {
            case 1 -> rating.setOneStarCount(rating.getOneStarCount() + increment);
            case 2 -> rating.setTwoStarCount(rating.getTwoStarCount() + increment);
            case 3 -> rating.setThreeStarCount(rating.getThreeStarCount() + increment);
            case 4 -> rating.setFourStarCount(rating.getFourStarCount() + increment);
            case 5 -> rating.setFiveStarCount(rating.getFiveStarCount() + increment);
        }
    }

    public RatingOverviewResponse getRatingOverview(Integer productId) {
        Rating rating = ratingRepository.findByProductId(productId).orElse(null);
        RatingOverviewResponse.RatingOverviewResponseBuilder builder = RatingOverviewResponse.builder()
                .productId(productId)
                .oneStarPercentage(0.0)
                .twoStarPercentage(0.0)
                .threeStarPercentage(0.0)
                .fourStarPercentage(0.0)
                .fiveStarPercentage(0.0)
                .totalRatings(0)
                .averageRating(0.0);

        if (rating != null) {
            int oneStarCount = rating.getOneStarCount();
            int twoStarCount = rating.getTwoStarCount();
            int threeStarCount = rating.getThreeStarCount();
            int fourStarCount = rating.getFourStarCount();
            int fiveStarCount = rating.getFiveStarCount();

            int totalRatings = oneStarCount + twoStarCount + threeStarCount + fourStarCount + fiveStarCount;

            builder.totalRatings(totalRatings);

            if (totalRatings > 0) {
                builder.oneStarPercentage((double) oneStarCount / totalRatings * 100)
                        .twoStarPercentage((double) twoStarCount / totalRatings * 100)
                        .threeStarPercentage((double) threeStarCount / totalRatings * 100)
                        .fourStarPercentage((double) fourStarCount / totalRatings * 100)
                        .fiveStarPercentage((double) fiveStarCount / totalRatings * 100)
                        .averageRating((oneStarCount * 1.0 + twoStarCount * 2.0 + threeStarCount * 3.0 +
                                fourStarCount * 4.0 + fiveStarCount * 5.0) / totalRatings);
            }
        }

        return builder.build();
    }
}