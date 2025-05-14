package com.petshop.petopia.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.petshop.petopia.model.review.ProductRating;
import com.petshop.petopia.model.review.Review;
import com.petshop.petopia.model.review.Rating;
import com.petshop.petopia.model.product.Product;
import com.petshop.petopia.model.user.User;
import com.petshop.petopia.repository.ProductRatingRepository;
import com.petshop.petopia.repository.product.ProductRepository;
import com.petshop.petopia.repository.RatingRepository;
import com.petshop.petopia.repository.ReviewRepository;
import com.petshop.petopia.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRatingRepository productRatingRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // Tách riêng chức năng gửi rating
    @Transactional
    public void submitProductRating(Integer productId, Integer userId, Integer ratingValue) {
        Optional<Product> productOptional = productRepository.findById(productId);
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<Rating> ratingOptional = ratingRepository.findById(ratingValue);

        if (productOptional.isPresent() && userOptional.isPresent() && ratingOptional.isPresent()) {
            Product product = productOptional.get();
            User user = userOptional.get();
            Rating rating = ratingOptional.get();

            // Lưu rating vào bảng product_ratings
            ProductRating productRating = new ProductRating();
            productRating.setProduct(product);
            productRating.setUser(user);
            productRating.setRating(rating);
            productRating.setCreatedAt(new Date());
            productRatingRepository.save(productRating);

            // Cập nhật thông tin rating tổng hợp
            updateProductRatingInfo(product);
        } else {
            throw new IllegalArgumentException("Product, User, or Rating not found");
        }
    }

    // Tách riêng chức năng gửi bình luận (review)
    @Transactional
    public void submitProductComment(Integer productId, Integer userId, String comment) {
        Optional<Product> productOptional = productRepository.findById(productId);
        Optional<User> userOptional = userRepository.findById(userId);

        if (productOptional.isPresent() && userOptional.isPresent()) {
            Product product = productOptional.get();
            User user = userOptional.get();

            Review review = new Review();
            review.setUser(user);
            review.setProduct(product);
            review.setComment(comment);
            review.setCreatedAt(new Date());
            reviewRepository.save(review);
        } else {
            throw new IllegalArgumentException("Product or User not found");
        }
    }

    @Transactional
    public void updateProductRatingInfo(Product product) {
        List<ProductRating> productRatings = productRatingRepository.findByProduct(product);

        if (productRatings.isEmpty()) {
            product.setAverageRating(0.0);
            product.setTotalRatings(0);
            product.setRatingCounts(objectMapper.createObjectNode());
        } else {
            int totalStars = 0;
            Map<Integer, Integer> ratingCountsMap = new HashMap<>();

            for (ProductRating pr : productRatings) {
                Integer ratingValue = pr.getRating().getId();
                totalStars += ratingValue;
                ratingCountsMap.put(ratingValue, ratingCountsMap.getOrDefault(ratingValue, 0) + 1);
            }

            double averageRating = (double) totalStars / productRatings.size();
            product.setAverageRating(averageRating);
            product.setTotalRatings(productRatings.size());

            ObjectNode ratingCountsNode = objectMapper.createObjectNode();
            ratingCountsMap.forEach((key, value) -> ratingCountsNode.putPOJO(String.valueOf(key), value));
            product.setRatingCounts(ratingCountsNode);
        }
        productRepository.save(product);
    }

    public Map<Integer, Double> calculateRatingPercentages(Integer productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            Map<Integer, Double> ratingPercentages = new HashMap<>();
            int totalRatings = product.getTotalRatings();

            if (totalRatings > 0 && product.getRatingCounts() != null) {
                try {
                    ObjectNode ratingCountsNode = (ObjectNode) product.getRatingCounts();
                    for (int i = 1; i <= 5; i++) {
                        int count = ratingCountsNode.has(String.valueOf(i)) ? ratingCountsNode.get(String.valueOf(i)).asInt() : 0;
                        double percentage = (double) count / totalRatings * 100;
                        ratingPercentages.put(i, percentage);
                    }
                } catch (Exception e) {
                    // Handle potential JSON parsing errors
                    e.printStackTrace();
                }
            }
            return ratingPercentages;
        }
        return new HashMap<>();
    }

    public Double calculateAverageRating(Integer productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            return productOptional.get().getAverageRating();
        }
        return 0.0;
    }

    public Integer calculateTotalRatingPoints(Integer productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            return productOptional.get().getTotalRatings();
        }
        return 0;
    }

    public Map<String, Object> getProductReviewsWithPagination(Integer productId, int page, int size) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            Pageable pageable = PageRequest.of(page, size);
            Page<Review> reviewPage = reviewRepository.findByProduct(product, pageable); // Corrected line
            List<Review> reviews = reviewPage.getContent();

            Map<String, Object> result = new HashMap<>();
            result.put("reviews", reviews);
            result.put("currentPage", reviewPage.getNumber());
            result.put("totalItems", reviewPage.getTotalElements());
            result.put("totalPages", reviewPage.getTotalPages());

            return result;
        } else {
            throw new IllegalArgumentException("Product not found");
        }
    }
}

