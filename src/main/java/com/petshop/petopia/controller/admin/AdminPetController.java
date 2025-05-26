package com.petshop.petopia.controller.admin;

import com.petshop.petopia.dto.request.admin.PetCreateRequest;
import com.petshop.petopia.dto.request.admin.ProductCreateRequest;
import com.petshop.petopia.dto.response.admin.PetCreateResponse;
import com.petshop.petopia.dto.response.product.ProductResponse;
import com.petshop.petopia.service.BannerService;
import com.petshop.petopia.service.PetService;
import com.petshop.petopia.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminPetController {

    private final PetService petService;
    private final ProductService productService;
    private final BannerService bannerService;

    @PostMapping(value = "/pet/add", consumes = {"multipart/form-data"})
    public ResponseEntity<?> createPet(@ModelAttribute PetCreateRequest petRequest) {
        try {
            PetCreateResponse createdPet = petService.createPet(petRequest);
            return ResponseEntity.ok(createdPet);
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to upload image or save pet: " + e.getMessage());
        }
    }
}
