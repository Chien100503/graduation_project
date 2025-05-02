package com.petshop.petopia.repository.pet;

import com.petshop.petopia.model.pet.PetCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PetCategoryRepository extends JpaRepository<PetCategory, Integer> {
    Optional<PetCategory> findByName(String name);
}