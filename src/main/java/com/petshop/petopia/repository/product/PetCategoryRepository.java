package com.petshop.petopia.repository.product;

import com.petshop.petopia.model.product.PetCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PetCategoryRepository extends JpaRepository<PetCategory, Integer> {
    Optional<PetCategory> findByName(String name);
}