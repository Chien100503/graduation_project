package com.petshop.petopia.repository.product;

import com.petshop.petopia.model.review.Review;
import com.petshop.petopia.model.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByProduct(Product product);
    Page<Review> findByProduct(Product product, Pageable pageable);
}
