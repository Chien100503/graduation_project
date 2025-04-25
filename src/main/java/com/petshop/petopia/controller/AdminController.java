package com.petshop.petopia.controller;

import com.petshop.petopia.dto.request.admin.PetCreateRequest;
import com.petshop.petopia.dto.response.PetResponse;
import com.petshop.petopia.model.product.Pet;
import com.petshop.petopia.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final PetService petService;

    @PostMapping(value = "/addPets", consumes = {"multipart/form-data"})
    public ResponseEntity<?> createPet(@ModelAttribute PetCreateRequest petRequest) {
        try {
            PetResponse createdPet = petService.createPet(petRequest);
            return ResponseEntity.ok(createdPet);
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("❌ Failed to upload image or save pet: " + e.getMessage());
        }
    }
}
