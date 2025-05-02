package com.petshop.petopia.repository.pet;

import com.petshop.petopia.model.pet.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Integer> {
}