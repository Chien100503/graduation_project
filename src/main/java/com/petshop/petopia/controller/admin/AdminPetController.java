package com.petshop.petopia.controller.admin;

import com.petshop.petopia.dto.request.pet.CreateBreedRequest;
import com.petshop.petopia.dto.request.admin.CreateCategoryRequest;
import com.petshop.petopia.dto.request.pet.CreatePetRequest;
import com.petshop.petopia.dto.response.pet.CreateBreedResponse;
import com.petshop.petopia.dto.response.admin.CreateCategoryResponse;
import com.petshop.petopia.dto.response.admin.CreatePetResponse;
import com.petshop.petopia.service.category.PetCategoryService;
import com.petshop.petopia.service.admin.AdminPetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminPetController {
    private final AdminPetService adminPetService;
    private final PetCategoryService petCategoryService;

    @PostMapping(value = "/pet/add", consumes = {"multipart/form-data"})
    public ResponseEntity<?> createPet(@ModelAttribute CreatePetRequest petRequest) {
        try {
            CreatePetResponse createdPet = adminPetService.createPet(petRequest);
            return ResponseEntity.ok(createdPet);
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to upload image or save pet: " + e.getMessage());
        }
    }

    @PostMapping(value = "/pet-category/add", consumes = {"multipart/form-data"})
    public ResponseEntity<?> createPetCategory(@ModelAttribute CreateCategoryRequest petRequest) {
        try {
            CreateCategoryResponse createdPet = petCategoryService.createPetCategory(petRequest);
            return ResponseEntity.ok(createdPet);
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to upload image or save pet: " + e.getMessage());
        }
    }

    @PostMapping(value = "/pet-breed/add", consumes = {"multipart/form-data"})
    public ResponseEntity<?> createPetBreed(@ModelAttribute CreateBreedRequest petRequest) {
        CreateBreedResponse createdPet = petCategoryService.createBreed(petRequest);
        return ResponseEntity.ok(createdPet);
    }
}
