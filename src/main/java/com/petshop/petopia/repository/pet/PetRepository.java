package com.petshop.petopia.repository.pet;

import com.petshop.petopia.model.pet.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Integer>, JpaSpecificationExecutor<Pet> {
    Optional<Pet> findByIdAndStatusTrue(Integer id);
    List<Pet> findByStatusTrue();
    List<Pet> findByBannerIdAndStatusTrue(Integer id);
    List<Pet> findByPetCategory_NameAndStatusTrue(String name);
    List<Pet> findByBreed_NameAndStatusTrue(String name);
    List<Pet> findByPetCategory_IdAndStatusTrue(Integer categoryId);
    List<Pet> findByPetCategory_IdAndBreed_IdAndStatusTrue(Integer categoryId, Integer breedId);
}