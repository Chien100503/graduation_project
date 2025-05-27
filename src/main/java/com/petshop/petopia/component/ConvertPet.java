package com.petshop.petopia.component;

import com.petshop.petopia.dto.response.admin.CreatePetResponse;
import com.petshop.petopia.dto.response.pet.GetAllPetResponse;
import com.petshop.petopia.dto.response.pet.PetDetailResponse;
import com.petshop.petopia.model.pet.Pet;
import com.petshop.petopia.model.pet.PetImage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConvertPet {
    public CreatePetResponse convertToResponse(Pet pet) {
        List<String> imageUrls = pet.getPetImages() != null
                ? pet.getPetImages().stream()
                .map(PetImage::getImageUrl)
                .collect(Collectors.toList())
                : List.of();

        return new CreatePetResponse(
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

    public GetAllPetResponse convertToGetAllPetResponse(Pet pet) {
        String firstImageUrl = null;
        if (pet.getPetImages() != null && !pet.getPetImages().isEmpty()) {
            firstImageUrl = pet.getPetImages().getFirst().getImageUrl();
        }
        return new GetAllPetResponse(
                pet.getId(),
                pet.getName(),
                pet.getPrice(),
                firstImageUrl
        );
    }

    public PetDetailResponse convertToGetPetDetailResponse(Pet pet) {
        PetDetailResponse dto = new PetDetailResponse();
        dto.setId(pet.getId());

        if (pet.getPetCategory() != null) {
            dto.setPetCategoryName(pet.getPetCategory().getName());
        }
        if (pet.getBreed() != null) {
            dto.setBreedName(pet.getBreed().getName());
        }

        dto.setName(pet.getName());
        dto.setAge(pet.getAge());
        dto.setGender(pet.getGender());
        dto.setSize(pet.getSize());
        dto.setWeight(pet.getWeight());
        dto.setColor(pet.getColor());
        dto.setPrice(pet.getPrice());
        dto.setStatus(pet.getStatus());
        dto.setDescription(pet.getDescription());

        if (pet.getPetImages() != null && !pet.getPetImages().isEmpty()) {
            List<String> urls = new ArrayList<>();
            for (PetImage image : pet.getPetImages()) {
                if (image.getImageUrl() != null) {
                    urls.add(image.getImageUrl());
                }
            }
            dto.setImageUrls(urls);
        } else {
            dto.setImageUrls(new ArrayList<>());
        }
        return dto;
    }
}