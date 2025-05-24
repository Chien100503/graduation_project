package com.petshop.petopia.controller.user;

import com.petshop.petopia.dto.request.admin.ProductCreateRequest;
import com.petshop.petopia.dto.request.category.ProductFilterRequest;
import com.petshop.petopia.dto.response.product.BrandResponse;
import com.petshop.petopia.dto.response.product.ProductCategoryResponse;
import com.petshop.petopia.dto.response.product.ProductResponse;
import com.petshop.petopia.service.ProductCategoryService;
import com.petshop.petopia.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductCategoryService productCategoryService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> createProduct(
            @RequestPart("product") ProductCreateRequest productCreateRequest,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        try {
            if (files != null && !files.isEmpty()) {
                productCreateRequest.setFile(files);
            }
            ProductResponse createdProduct = productService.createProduct(productCreateRequest);
            return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Integer id) {
        Optional<ProductResponse> product = productService.getProductById(id);
        return product.map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<ProductCategoryResponse>> getAllProductCategories() {
        return ResponseEntity.ok(productCategoryService.getAllProductCategories());
    }

    @GetMapping("/brands")
    public ResponseEntity<List<BrandResponse>> getAllProductBrands() {
        return ResponseEntity.ok(productCategoryService.getAllProductBrands());
    }

    @PostMapping("/filter")
    public ResponseEntity<?> filterProducts(@RequestBody ProductFilterRequest filterRequest) {
        try {
            List<ProductResponse> result = productCategoryService.filterProducts(filterRequest);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}