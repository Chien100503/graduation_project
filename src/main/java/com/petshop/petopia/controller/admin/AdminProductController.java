package com.petshop.petopia.controller.admin;

import com.petshop.petopia.dto.request.product.CreateProductRequest;
import com.petshop.petopia.dto.response.product.ProductResponse;
import com.petshop.petopia.service.admin.AdminProductService;
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
public class AdminProductController {
    private final AdminProductService adminProductService;

    @PostMapping(value = "/product/add", consumes = {"multipart/form-data"})
    public ResponseEntity<?> createProduct(@ModelAttribute CreateProductRequest productRequest) {
        try {
            ProductResponse createProduct = adminProductService.createProduct(productRequest);
            return ResponseEntity.ok(createProduct);
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to upload image or save product: " + e.getMessage());
        }
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        boolean deleted = adminProductService.deleteProduct(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "/product/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Integer id,
            @RequestPart("product") CreateProductRequest productCreateRequest,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        try {
            if (files != null && !files.isEmpty()) {
                productCreateRequest.setFile(files);
            }
            ProductResponse updatedProduct = adminProductService.updateProduct(id, productCreateRequest);
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
