package com.petshop.petopia.controller;

import com.petshop.petopia.dto.response.pet.GetPetResponse;
import com.petshop.petopia.model.pet.Pet;
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

    @GetMapping
    public ResponseEntity<?> getPets() {
        try {
            List<GetPetResponse> pets = petService.getAllPets();

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
            GetPetResponse petDto = petService.getPetById(petId); // <-- Sửa kiểu dữ liệu trả về
            return ResponseEntity.ok(petDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("❌ Lỗi hệ thống khi lấy thông tin Pet: " + e.getMessage());
        }
    }
}