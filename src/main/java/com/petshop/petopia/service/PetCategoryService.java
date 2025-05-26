package com.petshop.petopia.service;

import com.petshop.petopia.dto.request.category.PetFilterRequest;
import com.petshop.petopia.dto.response.pet.BreedResponse;
import com.petshop.petopia.dto.response.pet.CategoryResponse;
import com.petshop.petopia.dto.response.pet.PetResponse;
import com.petshop.petopia.model.pet.Breed;
import com.petshop.petopia.model.pet.Pet;
import com.petshop.petopia.model.pet.PetCategory;
import com.petshop.petopia.model.pet.PetImage;
import com.petshop.petopia.repository.pet.BreedRepository;
import com.petshop.petopia.repository.pet.PetCategoryRepository;
import com.petshop.petopia.repository.pet.PetRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
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

        Specification<Pet> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("status"), true));

            if (StringUtils.hasText(filterRequest.getCategory())) {
                Join<Pet, PetCategory> petCategoryJoin = root.join("petCategory");
                predicates.add(cb.equal(petCategoryJoin.get("name"), filterRequest.getCategory()));
            }

            if (StringUtils.hasText(filterRequest.getBreed())) {
                Join<Pet, Breed> breedJoin = root.join("breed");
                predicates.add(cb.equal(breedJoin.get("name"), filterRequest.getBreed()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return petRepository.findAll(spec).stream()
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