package com.petshop.petopia.controller;

import com.petshop.petopia.dto.request.admin.PetCreateRequest;
import com.petshop.petopia.dto.request.admin.ProductCreateRequest;
import com.petshop.petopia.dto.response.PetCreateResponse;
import com.petshop.petopia.dto.response.ProductResponse;
import com.petshop.petopia.service.PetService;
import com.petshop.petopia.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final PetService petService;
    private final ProductService productService;

    @PostMapping(value = "/pet/add", consumes = {"multipart/form-data"})
    public ResponseEntity<?> createPet(@ModelAttribute PetCreateRequest petRequest) {
        try {
            PetCreateResponse createdPet = petService.createPet(petRequest);
            return ResponseEntity.ok(createdPet);
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("❌ Failed to upload image or save pet: " + e.getMessage());
        }
    }

    @PostMapping(value = "/product/add", consumes = {"multipart/form-data"})
    public ResponseEntity<?> createProduct(@ModelAttribute ProductCreateRequest productRequest) {
        try {
            ProductResponse createProduct = productService.createProduct(productRequest);
            return ResponseEntity.ok(createProduct);
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("❌ Failed to upload image or save product: " + e.getMessage());
        }
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        boolean deleted = productService.deleteProduct(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "/product/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Integer id,
            @RequestPart("product") ProductCreateRequest productCreateRequest,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        try {
            if (files != null && !files.isEmpty()) {
                productCreateRequest.setFile(files);
            }
            ProductResponse updatedProduct = productService.updateProduct(id, productCreateRequest);
            if (updatedProduct != null) {
                return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
