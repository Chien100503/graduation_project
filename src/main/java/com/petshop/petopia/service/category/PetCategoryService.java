package com.petshop.petopia.service.category;

import com.petshop.petopia.component.ConvertPet;
import com.petshop.petopia.dto.request.pet.CreateBreedRequest;
import com.petshop.petopia.dto.request.admin.CreateCategoryRequest;
import com.petshop.petopia.dto.request.category.PetFilterRequest;
import com.petshop.petopia.dto.response.pet.*;
import com.petshop.petopia.dto.response.admin.CreateCategoryResponse;
import com.petshop.petopia.model.pet.Breed;
import com.petshop.petopia.model.pet.Pet;
import com.petshop.petopia.model.pet.PetCategory;
import com.petshop.petopia.repository.pet.BreedRepository;
import com.petshop.petopia.repository.pet.PetCategoryRepository;
import com.petshop.petopia.repository.pet.PetRepository;
import com.petshop.petopia.repository.user.WishlistRepository;
import com.petshop.petopia.service.FirebaseService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetCategoryService {
    private final PetRepository petRepository;
    private final PetCategoryRepository petCategoryRepository;
    private final BreedRepository breedRepository;
    private final FirebaseService firebaseService;
    private final ConvertPet convertPet;
    private final WishlistRepository wishlistRepository;

    @Transactional
    public CreateCategoryResponse createPetCategory(CreateCategoryRequest createPetCategoryRequest) throws IOException {
        Optional<PetCategory> existingCategory = petCategoryRepository.findByName(createPetCategoryRequest.getName());
        if (existingCategory.isPresent()) {
            throw new IllegalArgumentException("Tên danh mục '" + createPetCategoryRequest.getName() + "' đã tồn tại.");
        }

        String imageUrl;
        MultipartFile file = createPetCategoryRequest.getFile();
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Ảnh danh mục là bắt buộc.");
        } else {
            imageUrl = firebaseService.uploadImageCategory(file);
        }

        PetCategory category = new PetCategory();
        category.setName(createPetCategoryRequest.getName());
        category.setImageUrl(imageUrl);
        PetCategory savedCategory = petCategoryRepository.save(category);
        return new CreateCategoryResponse(savedCategory.getId(), savedCategory.getName(), savedCategory.getImageUrl());
    }

    @Transactional
    public CreateBreedResponse createBreed(CreateBreedRequest req) {
        Optional<Breed> existingBreed = breedRepository.findByNameAndPetCategory_Name(req.getName(), req.getCategoryName());
        if (existingBreed.isPresent()) {
            throw new IllegalArgumentException("Giống '" + req.getName() + "' đã tồn tại trong danh mục '" + req.getCategoryName() + "'.");
        }

        PetCategory category = petCategoryRepository.findByName(req.getCategoryName())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy danh mục với tên: " + req.getCategoryName()));

        Breed newBreed = new Breed();
        newBreed.setName(req.getName());
        newBreed.setPetCategory(category);
        newBreed.setCreatedAt(new Date());
        Breed savedBreed = breedRepository.save(newBreed);

        return new CreateBreedResponse(
                savedBreed.getId(),
                savedBreed.getName(),
                savedBreed.getPetCategory().getName()
        );
    }

    @Transactional(readOnly = true)
    public List<GetAllPetResponse> getPetsByCategoryId(Integer userId, Integer categoryId) {
        List<Pet> pets = petRepository.findByPetCategory_IdAndStatusTrue(categoryId);
        return pets.stream()
                .map(pet -> {
                    boolean isInWishlist = wishlistRepository.existsByUserIdAndPetId(userId, pet.getId());
                    return convertPet.convertToGetAllPetResponse(pet, isInWishlist);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PetDetailResponse> filterActivePets(Integer userId, PetFilterRequest filterRequest) {
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
                .map(pet -> {
                    boolean isInWishlist = wishlistRepository.existsByUserIdAndPetId(userId, pet.getId());
                    return convertPet.convertToGetPetDetailResponse(pet, isInWishlist);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PetCategoryResponse> getAllCategories() {
        return petCategoryRepository.findAll().stream()
                .map(category -> new PetCategoryResponse(category.getId(), category.getName(), category.getImageUrl()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BreedResponse> getBreedsByCategory(Integer categoryId) {
        return breedRepository.findByPetCategoryId(categoryId).stream()
                .map(breed -> new BreedResponse(breed.getId(), breed.getName()))
                .collect(Collectors.toList());
    }
}
