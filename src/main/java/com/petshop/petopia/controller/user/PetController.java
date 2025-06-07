package com.petshop.petopia.controller.user;

import com.petshop.petopia.dto.request.category.PetFilterRequest;
import com.petshop.petopia.dto.response.pet.BreedResponse;
import com.petshop.petopia.dto.response.pet.GetAllPetResponse;
import com.petshop.petopia.dto.response.pet.PetCategoryResponse;
import com.petshop.petopia.dto.response.pet.PetDetailResponse;
import com.petshop.petopia.service.category.PetCategoryService;
import com.petshop.petopia.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pet")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;
    private final PetCategoryService petCategoryService;

    @GetMapping
    public ResponseEntity<?> getPets() {
        try {
            List<GetAllPetResponse> pets = petService.getAllPets();

            if (pets.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(pets);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to retrieve pets: " + e.getMessage());
        }
    }

    @GetMapping("/{petId}")
    public ResponseEntity<?> getPetDetails(@PathVariable("petId") Integer petId) {
        try {
            PetDetailResponse petDto = petService.getPetById(petId);
            return ResponseEntity.ok(petDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Lỗi hệ thống khi lấy thông tin Pet: " + e.getMessage());
        }
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<GetAllPetResponse>> getPetsByCategoryId(@PathVariable Integer categoryId) {
        List<GetAllPetResponse> pets = petCategoryService.getPetsByCategoryId(categoryId);
        if (pets.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/category/{categoryId}/breeds/{breedId}")
    public ResponseEntity<List<GetAllPetResponse>> getPetsByCategoryAndBreed(
            @PathVariable Integer categoryId,
            @PathVariable Integer breedId) {
        List<GetAllPetResponse> pets = petService.getPetByBreed(categoryId, breedId);
        if (!pets.isEmpty()) {
            return new ResponseEntity<>(pets, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterActivePets(@RequestBody PetFilterRequest filterRequest) {
        try {
            List<PetDetailResponse> result = petCategoryService.filterActivePets(filterRequest);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/category")
    public ResponseEntity<List<PetCategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(petCategoryService.getAllCategories());
    }

    @GetMapping("/category/{categoryId}/breeds")
    public ResponseEntity<List<BreedResponse>> getBreedsByCategory(
            @PathVariable Integer categoryId) {
        return ResponseEntity.ok(petCategoryService.getBreedsByCategory(categoryId));
    }
}