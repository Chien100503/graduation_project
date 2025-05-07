package com.petshop.petopia.controller;

import com.petshop.petopia.dto.request.admin.PetCreateRequest;
import com.petshop.petopia.dto.request.admin.ProductCreateRequest;
import com.petshop.petopia.dto.response.PetCreateResponse;
import com.petshop.petopia.dto.response.ProductResponse;
import com.petshop.petopia.service.PetService;
import com.petshop.petopia.service.ProductService;
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
    private final ProductService productService;

    @PostMapping(value = "/addPet", consumes = {"multipart/form-data", "application/json"})
    public ResponseEntity<?> createPet(@ModelAttribute PetCreateRequest petRequest) {
        try {
            PetCreateResponse createdPet = petService.createPet(petRequest);
            return ResponseEntity.ok(createdPet);
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("❌ Failed to upload image or save pet: " + e.getMessage());
        }
    }

    @PostMapping(value = "/addProduct", consumes = {"multipart/form-data", "application/json"})
    public ResponseEntity<?> createProduct(@ModelAttribute ProductCreateRequest productRequest) {
        try {
            ProductResponse createProduct = productService.createProduct(productRequest);
            return ResponseEntity.ok(createProduct);
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("❌ Failed to upload image or save product: " + e.getMessage());
        }
    }
}
