package com.petshop.petopia.controller;

import com.petshop.petopia.dto.request.PetCreateRequest;
import com.petshop.petopia.model.Pet;
import com.petshop.petopia.repository.PetRepository;
import com.petshop.petopia.service.FirebaseService;
import com.petshop.petopia.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;
    private final FirebaseService firebaseService;
    private final PetRepository petRepository;

    @PostMapping(value = "/addPets", consumes = {"multipart/form-data"})
    public ResponseEntity<?> createPet(@ModelAttribute PetCreateRequest petRequest) {
        try {
            // Upload image lên Firebase
            String imageUrl = firebaseService.uploadFile(petRequest.getFile());

            // Tạo đối tượng Pet từ request
            Pet pet = petService.buildPetFromRequest(petRequest, imageUrl);

            // Lưu vào DB
            Pet saved = petRepository.save(pet);
            return ResponseEntity.ok(saved);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to upload image or save pet");
        }
    }
}
