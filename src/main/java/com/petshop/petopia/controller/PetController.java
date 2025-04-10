package com.petshop.petopia.controller;

import com.petshop.petopia.dto.request.PetCreateRequest;
import com.petshop.petopia.model.Pet;
import com.petshop.petopia.service.FirebaseService;
import com.petshop.petopia.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;
    private final FirebaseService firebaseService;

    @PostMapping(value = "/admin/addPets", consumes = {"multipart/form-data"})
    public ResponseEntity<?> createPet(@ModelAttribute PetCreateRequest petRequest) {
        try {
            String imageUrl = firebaseService.upload(petRequest.getFile());

            Pet createdPet = petService.createPet(petRequest, imageUrl);
            return ResponseEntity.ok(createdPet);

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("❌ Failed to upload image or save pet: " + e.getMessage());
        }
    }
}