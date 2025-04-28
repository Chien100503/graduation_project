package com.petshop.petopia.util;

import com.petshop.petopia.dto.response.PetResponse;
import com.petshop.petopia.model.product.Pet;
import org.springframework.stereotype.Component;

@Component
public class ConvertPet {
    public PetResponse convertToResponse(Pet pet) {
        return new PetResponse(
                pet.getId(),
                pet.getName(),
                pet.getBreed(),
                pet.getAge(),
                pet.getGender(),
                pet.getSize(),
                pet.getWeight(),
                pet.getColor(),
                pet.getPrice(),
                pet.getStatus() != null && pet.getStatus() ? "Available" : "Ordered",
                pet.getHealthStatus(),
                pet.getDescription(),
                pet.getImg(),
                pet.getCreatedAt(),
                pet.getUpdatedAt(),
                pet.getPetCategory() != null ? pet.getPetCategory().getName() : null
        );
    }
}
