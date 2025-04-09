package com.petshop.petopia.repository;

import com.petshop.petopia.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {
}