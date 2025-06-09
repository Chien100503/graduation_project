package com.petshop.petopia.controller.user;

import com.petshop.petopia.dto.request.category.ProductFilterRequest;
import com.petshop.petopia.dto.response.product.*;
import com.petshop.petopia.security.JwtService;
import com.petshop.petopia.service.ProductService;
import com.petshop.petopia.service.category.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ProductCategoryService productCategoryService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<?> getAllProducts(@RequestHeader("Authorization") String token) {
        try {
            Integer userId = jwtService.extractUserId(token);
            List<GetAllProductResponse> products = productService.getAllProducts(userId);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to retrieve products: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(
            @PathVariable Integer id,
            @RequestHeader("Authorization") String token) {
        try {
            Integer userId = jwtService.extractUserId(token);
            GetProductDetailResponse product = productService.getProductDetail(userId, id);
            return ResponseEntity.ok(product);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to retrieve product: " + e.getMessage());
        }
    }

    @GetMapping("/category")
    public ResponseEntity<List<ProductCategoryResponse>> getAllProductCategories() {
        return ResponseEntity.ok(productCategoryService.getAllProductCategories());
    }

    @GetMapping("/brands")
    public ResponseEntity<List<GetBrandResponse>> getAllBrands() {
        return ResponseEntity.ok(productCategoryService.getAllBrands());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getProductByCategory(
            @PathVariable Integer categoryId,
            @RequestHeader("Authorization") String token) {
        try {
            Integer userId = jwtService.extractUserId(token);
            List<GetAllProductResponse> responses = productService.getProductsByCategory(userId, categoryId);
            return responses.isEmpty() ?
                    ResponseEntity.noContent().build() :
                    ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to retrieve products by category: " + e.getMessage());
        }
    }

    @GetMapping("/category/{categoryId}/type")
    public ResponseEntity<?> getTypesByCategory(@PathVariable Integer categoryId) {
        try {
            Optional<List<TypeResponse>> types = productService.getTypesByCategoryId(categoryId);
            if (types == null || types.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(types);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to retrieve types: " + e.getMessage());
        }
    }

    @GetMapping("/category/{categoryId}/type/{typeId}")
    public ResponseEntity<?> getProductsByCategoryAndType(
            @PathVariable Integer categoryId,
            @PathVariable Integer typeId,
            @RequestHeader("Authorization") String token) {
        try {
            Integer userId = jwtService.extractUserId(token);
            List<GetAllProductResponse> products = productService.getProductsByCategoryAndType(userId, categoryId, typeId);
            return products.isEmpty() ?
                    ResponseEntity.noContent().build() :
                    ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to retrieve filtered products: " + e.getMessage());
        }
    }

    @PostMapping("/filter")
    public ResponseEntity<?> filterProducts(
            @RequestBody ProductFilterRequest filterRequest,
            @RequestHeader("Authorization") String token
    ) {
        try {
            Integer userId = jwtService.extractUserId(token);
            List<GetAllProductResponse> result = productCategoryService.filterProducts(userId, filterRequest);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
