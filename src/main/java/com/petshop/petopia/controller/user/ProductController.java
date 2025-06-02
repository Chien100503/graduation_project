package com.petshop.petopia.controller.user;

import com.petshop.petopia.dto.request.product.CreateProductRequest;
import com.petshop.petopia.dto.request.category.ProductFilterRequest;
import com.petshop.petopia.dto.response.product.BrandResponse;
import com.petshop.petopia.dto.response.product.ProductCategoryResponse;
import com.petshop.petopia.dto.response.product.ProductResponse;
import com.petshop.petopia.dto.response.product.TypeResponse;
import com.petshop.petopia.model.product.Product;
import com.petshop.petopia.model.product.Type;
import com.petshop.petopia.service.category.ProductCategoryService;
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

    @GetMapping("/category")
    public ResponseEntity<List<ProductCategoryResponse>> getAllProductCategories() {
        return ResponseEntity.ok(productCategoryService.getAllProductCategories());
    }

    @GetMapping("/brands")
    public ResponseEntity<List<BrandResponse>> getAllBrands() {
        return ResponseEntity.ok(productCategoryService.getAllBrands());
    }

    @GetMapping("/category/{categoryId}/type")
    public ResponseEntity<List<TypeResponse>> getTypesByCategory(@PathVariable Integer categoryId) {
        Optional<List<TypeResponse>> typesResponse = productService.getTypesByCategoryId(categoryId);
        return typesResponse.map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/category/{categoryId}/type/{typeId}")
    public ResponseEntity<List<ProductCategoryResponse>> getProductsByCategoryAndType(
            @PathVariable Integer categoryId,
            @PathVariable Integer typeId) {
        List<ProductCategoryResponse> products = productService.getProductsByCategoryAndType(categoryId, typeId);
        if (!products.isEmpty()) {
            return new ResponseEntity<>(products, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterProducts(@RequestBody ProductFilterRequest filterRequest) {
        try {
            List<ProductResponse> result = productCategoryService.filterProducts(filterRequest);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}