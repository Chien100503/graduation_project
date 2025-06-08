package com.petshop.petopia.component;

import com.petshop.petopia.dto.response.admin.CreatePetResponse;
import com.petshop.petopia.dto.response.pet.GetAllPetResponse;
import com.petshop.petopia.dto.response.pet.PetDetailResponse;
import com.petshop.petopia.model.pet.Pet;
import com.petshop.petopia.model.pet.PetImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ConvertPet {
    private final CalculatePrice calculatePrice;

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
                pet.getThumbnail(),
                imageUrls,
                pet.getCreatedAt(),
                pet.getUpdatedAt(),
                pet.getPetCategory() != null ? pet.getPetCategory().getName() : null
        );
    }

    public GetAllPetResponse convertToGetAllPetResponse(Pet pet) {
        BigDecimal salePercent = BigDecimal.ZERO;
        if (pet.getBanner() != null && pet.getBanner().getSalePercent() != null) {
            salePercent = pet.getBanner().getSalePercent();
        }
        return new GetAllPetResponse(
                pet.getId(),
                pet.getName(),
                pet.getThumbnail(),
                pet.getDescription(),
                pet.getBreed().getName(),
                salePercent,
                pet.getPrice(),
                calculatePrice.calculatePriceDiscount(pet.getPrice(), salePercent)
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