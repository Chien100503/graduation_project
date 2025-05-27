package com.petshop.petopia.repository.pet;

import com.petshop.petopia.model.pet.Breed;
import com.petshop.petopia.model.pet.PetCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BreedRepository extends JpaRepository<Breed, Integer> {
    Optional<Breed> findByName(String name);
    List<Breed> findByPetCategoryId(Integer categoryId);
    Optional<Breed> findByNameAndPetCategory_Name(String name, String categoryName);
    Optional<Breed> findByNameAndPetCategory(String name, PetCategory petCategory); // Thêm phương thức này
}