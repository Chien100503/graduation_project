package com.petshop.petopia.controller.user;

import com.petshop.petopia.dto.request.category.PetFilterRequest;
import com.petshop.petopia.dto.response.pet.BreedResponse;
import com.petshop.petopia.dto.response.pet.CategoryResponse;
import com.petshop.petopia.dto.response.pet.PetResponse;
import com.petshop.petopia.service.PetCategoryService;
import com.petshop.petopia.service.PetService;
import lombok.RequiredArgsConstructor;
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
            List<PetResponse> pets = petService.getAllPets();

            if (pets.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(pets);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("❌ Failed to retrieve pets: " + e.getMessage());
        }
    }

    @GetMapping("/{petId}")
    public ResponseEntity<?> getPetDetails(@PathVariable("petId") Integer petId) { // Nên dùng Integer cho PathVariable ID để dễ handle null (mặc dù ở đây luôn có giá trị)
        try {
            PetResponse petDto = petService.getPetById(petId); // <-- Sửa kiểu dữ liệu trả về
            return ResponseEntity.ok(petDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("❌ Lỗi hệ thống khi lấy thông tin Pet: " + e.getMessage());
        }
    }

    @PostMapping("/filter")
    public ResponseEntity<?> filterActivePets(@RequestBody PetFilterRequest filterRequest) {
        try {
            List<PetResponse> result = petCategoryService.filterActivePets(filterRequest);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(petCategoryService.getAllCategories());
    }

    @GetMapping("/categories/{categoryId}/breeds")
    public ResponseEntity<List<BreedResponse>> getBreedsByCategory(
            @PathVariable Integer categoryId) {
        return ResponseEntity.ok(petCategoryService.getBreedsByCategory(categoryId));
    }
}