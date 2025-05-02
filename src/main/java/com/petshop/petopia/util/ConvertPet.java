package com.petshop.petopia.util;

import com.petshop.petopia.dto.response.PetResponse;
import com.petshop.petopia.model.pet.Pet;
import com.petshop.petopia.model.pet.PetImage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConvertPet {
    public PetResponse convertToResponse(Pet pet) {
        List<String> imageUrls = pet.getPetImages() != null
                ? pet.getPetImages().stream()
                .map(PetImage::getImageUrl)
                .collect(Collectors.toList())
                : List.of();

        return new PetResponse(
                pet.getId(),
                pet.getName(),
                pet.getBreed() != null ? pet.getBreed().getName() : null,
                pet.getAge(),
                pet.getGender(),
                pet.getSize(),
                pet.getWeight(),
                pet.getColor(),
                pet.getPrice(),
                pet.getStatus() != null && pet.getStatus() ? "Available" : "Ordered",
                pet.getDescription(),
                imageUrls,
                pet.getCreatedAt(),
                pet.getUpdatedAt(),
                pet.getPetCategory() != null ? pet.getPetCategory().getName() : null
        );
    }
}