package com.petshop.petopia.controller.user;

import com.petshop.petopia.dto.request.category.PetFilterRequest;
import com.petshop.petopia.dto.response.pet.BreedResponse;
import com.petshop.petopia.dto.response.pet.GetAllPetResponse;
import com.petshop.petopia.dto.response.pet.PetCategoryResponse;
import com.petshop.petopia.dto.response.pet.PetDetailResponse;
import com.petshop.petopia.security.JwtService;
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
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<?> getPets(
            @RequestHeader("Authorization") String token
    ) {
        Integer userId = jwtService.extractUserId(token);
        try {
            List<GetAllPetResponse> pets = petService.getAllPets(userId);

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
    public ResponseEntity<?> getPetDetails(
            @PathVariable("petId") Integer petId,
            @RequestHeader("Authorization") String token
    ) {
        Integer userId = jwtService.extractUserId(token);
        try {
            PetDetailResponse petDto = petService.getPetById(userId, petId);
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
    public ResponseEntity<List<GetAllPetResponse>> getPetsByCategoryId(
            @PathVariable Integer categoryId,
            @RequestHeader("Authorization") String token
    ) {
        Integer userId = jwtService.extractUserId(token);
        List<GetAllPetResponse> pets = petCategoryService.getPetsByCategoryId(userId, categoryId);
        if (pets.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/category/{categoryId}/breeds/{breedId}")
    public ResponseEntity<List<GetAllPetResponse>> getPetsByCategoryAndBreed(
            @PathVariable Integer categoryId,
            @PathVariable Integer breedId,
            @RequestHeader("Authorization") String token
    ) {
        Integer userId = jwtService.extractUserId(token);
        List<GetAllPetResponse> pets = petService.getPetByBreed(userId, categoryId, breedId);
        if (!pets.isEmpty()) {
            return new ResponseEntity<>(pets, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterActivePets(
            @RequestHeader("Authorization") String token,
            @RequestBody PetFilterRequest filterRequest
    ) {
        Integer userId = jwtService.extractUserId(token);
        try {
            List<PetDetailResponse> result = petCategoryService.filterActivePets(userId, filterRequest);
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
