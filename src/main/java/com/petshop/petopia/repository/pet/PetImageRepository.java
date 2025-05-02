package com.petshop.petopia.repository.pet;

import com.petshop.petopia.model.pet.PetImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetImageRepository extends JpaRepository<PetImage, Integer> {
}
