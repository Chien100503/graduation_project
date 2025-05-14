package com.petshop.petopia.repository;

import com.petshop.petopia.model.review.ProductRating;
import com.petshop.petopia.model.product.Product;
import com.petshop.petopia.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRatingRepository extends JpaRepository<ProductRating, Integer> {
    List<ProductRating> findByProduct(Product product);
    Optional<ProductRating> findByProductAndUser(Product product, User user);
}
