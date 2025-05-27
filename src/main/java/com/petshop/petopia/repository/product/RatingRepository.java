package com.petshop.petopia.repository.product;

import com.petshop.petopia.model.review.Rating;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends CrudRepository<Rating, Integer> {
    Optional<Rating> findByProductId(Integer productId);
}
