package com.petshop.petopia.repository.product;

import com.petshop.petopia.model.product.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Integer> {
}