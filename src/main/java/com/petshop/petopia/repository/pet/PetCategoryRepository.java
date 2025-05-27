package com.petshop.petopia.repository.pet;

import com.petshop.petopia.model.pet.Pet;
import com.petshop.petopia.model.pet.PetCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PetCategoryRepository extends JpaRepository<PetCategory, Integer>, JpaSpecificationExecutor<Pet> {
    Optional<PetCategory> findByName(String name);
}