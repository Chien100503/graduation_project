package com.petshop.petopia.repository.pet;

import com.petshop.petopia.model.pet.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Integer>, JpaSpecificationExecutor<Pet> {
    List<Pet> findByBannerId(Integer id);
    List<Pet> findByPetCategory_Name(String name);
    List<Pet> findByBreed_Name(String name);
    List<Pet> findByPetCategory_Id(Integer categoryId);
    List<Pet> findByPetCategory_IdAndBreed_Id(Integer categoryId, Integer breedId);
}