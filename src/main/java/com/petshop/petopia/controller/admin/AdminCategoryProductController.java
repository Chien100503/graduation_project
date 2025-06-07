package com.petshop.petopia.controller.admin;

import com.petshop.petopia.dto.request.admin.CreateCategoryRequest;
import com.petshop.petopia.dto.request.product.CreateBrandRequest;
import com.petshop.petopia.dto.request.product.CreateTypeRequest;
import com.petshop.petopia.dto.response.admin.CreateCategoryResponse;
import com.petshop.petopia.dto.response.product.GetBrandResponse;
import com.petshop.petopia.dto.response.product.CreateTypeResponse;
import com.petshop.petopia.service.category.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminCategoryProductController {
    private final ProductCategoryService productCategoryService;

    @PostMapping("/product-type/add")
    public ResponseEntity<CreateTypeResponse> createType(@RequestBody CreateTypeRequest req) {
        CreateTypeResponse typeResponse = productCategoryService.createType(req);
        return new ResponseEntity<>(typeResponse, HttpStatus.CREATED);
    }

    @PostMapping("/product-brand/add")
    public ResponseEntity<GetBrandResponse> createBrand(@RequestBody CreateBrandRequest req) {
        GetBrandResponse brandResponse = productCategoryService.createBrand(req);
        return new ResponseEntity<>(brandResponse, HttpStatus.CREATED);
    }

    @PostMapping(value = "/product-category/add", consumes = {"multipart/form-data"})
    public ResponseEntity<?> createProductCategory(@ModelAttribute CreateCategoryRequest petRequest) {
        try {
            CreateCategoryResponse createdPet = productCategoryService.createProductCategory(petRequest);
            return ResponseEntity.ok(createdPet);
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to upload image or save pet: " + e.getMessage());
        }
    }
}
