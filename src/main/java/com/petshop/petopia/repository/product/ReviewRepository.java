package com.petshop.petopia.repository.product;

import com.petshop.petopia.model.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByProductId(Integer productId);
}
