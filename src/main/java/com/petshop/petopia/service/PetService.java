package com.petshop.petopia.service;

import com.petshop.petopia.dto.request.PetCreateRequest;
import com.petshop.petopia.model.Pet;
import com.petshop.petopia.repository.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PetService {

    private final ProductCategoryRepository categoryRepository;

    public Pet buildPetFromRequest(PetCreateRequest req, String imageUrl) {
        Pet pet = new Pet();
        pet.setName(req.getName());
        pet.setBreed(req.getBreed());
        pet.setAge(req.getAge());
        pet.setGender(req.getGender());
        pet.setSize(req.getSize());
        pet.setWeight(req.getWeight());
        pet.setColor(req.getColor());
        pet.setPrice(req.getPrice());
        pet.setStatus(req.getStatus());
        pet.setHealthStatus(req.getHealthStatus());
        pet.setDescription(req.getDescription());
        pet.setImg(imageUrl);
        pet.setPrCategory(categoryRepository.findById(req.getProductCategoryId()).orElse(null));
        return pet;
    }
}