package com.petshop.petopia.service;

import com.petshop.petopia.dto.request.category.PetFilterRequest;
import com.petshop.petopia.dto.response.pet.BreedResponse;
import com.petshop.petopia.dto.response.pet.CategoryResponse;
import com.petshop.petopia.dto.response.pet.PetResponse;
import com.petshop.petopia.model.pet.Pet;
import com.petshop.petopia.model.pet.PetImage;
import com.petshop.petopia.repository.pet.BreedRepository;
import com.petshop.petopia.repository.pet.PetCategoryRepository;
import com.petshop.petopia.repository.pet.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetCategoryService {

    private final PetRepository petRepository;
    private final PetCategoryRepository petCategoryRepository;
    private final BreedRepository breedRepository;

    @Transactional(readOnly = true)
    public List<PetResponse> filterActivePets(PetFilterRequest filterRequest) {
        filterRequest.validate();

        Specification<Pet> spec = Specification.where(isActive()); // Chỉ lấy pet có status = true

        if (filterRequest.getCategory() != null && !filterRequest.getCategory().isBlank()) {
            spec = spec.and(hasCategory(filterRequest.getCategory()));
        }

        if (filterRequest.getBreed() != null && !filterRequest.getBreed().isBlank()) {
            spec = spec.and(hasBreed(filterRequest.getBreed()));
        }

        return petCategoryRepository.findAll(spec).stream()
                .map(this::mapToPetResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return petCategoryRepository.findAll().stream()
                .map(category -> new CategoryResponse(category.getId(), category.getName()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BreedResponse> getBreedsByCategory(Integer categoryId) {
        return breedRepository.findByPetCategoryId(categoryId).stream()
                .map(breed -> new BreedResponse(breed.getId(), breed.getName()))
                .collect(Collectors.toList());
    }

    private Specification<Pet> isActive() {
        return (root, query, cb) -> cb.equal(root.get("status"), true);
    }

    private Specification<Pet> hasCategory(String categoryName) {
        return (root, query, cb) ->
                cb.equal(root.get("petCategory").get("name"), categoryName);
    }

    private Specification<Pet> hasBreed(String breedName) {
        return (root, query, cb) ->
                cb.equal(root.get("breed").get("name"), breedName);
    }

    private PetResponse mapToPetResponse(Pet pet) {
        List<String> imageUrls = pet.getPetImages().stream()
                .map(PetImage::getImageUrl)
                .collect(Collectors.toList());

        return new PetResponse(
                pet.getId(),
                pet.getPetCategory() != null ? pet.getPetCategory().getName() : null,
                pet.getBreed() != null ? pet.getBreed().getName() : null,
                pet.getName(),
                pet.getAge(),
                pet.getGender(),
                pet.getSize(),
                pet.getWeight(),
                pet.getColor(),
                pet.getPrice(),
                pet.getStatus(),
                pet.getDescription(),
                imageUrls
        );
    }
}
