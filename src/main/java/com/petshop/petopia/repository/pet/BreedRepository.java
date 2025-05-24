package com.petshop.petopia.repository.pet;

import com.petshop.petopia.model.pet.Breed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BreedRepository extends JpaRepository<Breed, Integer> {
    Optional<Breed> findByName(String name);
    List<Breed> findByPetCategoryId(Integer categoryId);
}