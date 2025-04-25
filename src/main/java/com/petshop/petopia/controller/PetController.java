package com.petshop.petopia.controller;

import com.petshop.petopia.dto.request.admin.PetCreateRequest;
import com.petshop.petopia.model.product.Pet;
import com.petshop.petopia.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @GetMapping("/pet")
    public ResponseEntity<?> getPets() {
        try {
            List<Pet> pets = petService.getAllPets();

            if (pets.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(pets);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("❌ Failed to retrieve pets: " + e.getMessage());
        }
    }


}