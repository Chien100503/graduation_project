package com.petshop.petopia.controller.admin;

import com.petshop.petopia.dto.request.banner.CUBannerRequest;
import com.petshop.petopia.dto.response.banner.*;
import com.petshop.petopia.service.admin.AdminBannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/admin/banner")
@RequiredArgsConstructor
public class AdminBannerController {

    private final AdminBannerService adminBannerService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createBanner(@ModelAttribute CUBannerRequest request) {
        try {
            validateBannerDates(request);
            CUBannerResponse response = adminBannerService.createBanner(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Lỗi khi upload ảnh");
        }
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateBanner(
            @PathVariable Integer id,
            @ModelAttribute CUBannerRequest request) {
        try {
            validateBannerDates(request);
            CUBannerResponse response = adminBannerService.updateBanner(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Lỗi khi cập nhật ảnh");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBanner(@PathVariable Integer id) {
        try {
            adminBannerService.deleteBanner(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/{bannerId}/pets/{petId}")
    public ResponseEntity<?> addPetToBanner(
            @PathVariable Integer bannerId,
            @PathVariable Integer petId) {
        try {
            adminBannerService.addPetToBanner(bannerId, petId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/pets/{petId}")
    public ResponseEntity<?> removePetFromBanner(@PathVariable Integer petId) {
        try {
            adminBannerService.removePetFromBanner(petId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/{bannerId}/products/{productId}")
    public ResponseEntity<?> addProductToBanner(
            @PathVariable Integer bannerId,
            @PathVariable Integer productId) {
        try {
            adminBannerService.addProductToBanner(bannerId, productId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<?> removeProductFromBanner(@PathVariable Integer productId) {
        try {
            adminBannerService.removeProductFromBanner(productId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/{bannerId}/pets")
    public ResponseEntity<?> addAllPetsToBanner(@PathVariable Integer bannerId) {
        try {
            adminBannerService.addAllPetsToBanner(bannerId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{bannerId}/products")
    public ResponseEntity<?> addAllProductsToBanner(@PathVariable Integer bannerId) {
        try {
            adminBannerService.addAllProductsToBanner(bannerId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{bannerId}/pets/category/{categoryName}")
    public ResponseEntity<?> addPetsByCategoryToBanner(
            @PathVariable Integer bannerId,
            @PathVariable String categoryName) {
        try {
            adminBannerService.addPetsByCategoryToBanner(bannerId, categoryName);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{bannerId}/pets/breed/{breedName}")
    public ResponseEntity<?> addPetsByBreedToBanner(
            @PathVariable Integer bannerId,
            @PathVariable String breedName) {
        try {
            adminBannerService.addPetsByBreedToBanner(bannerId, breedName);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{bannerId}/products/category/{categoryName}")
    public ResponseEntity<?> addProductsByCategoryToBanner(
            @PathVariable Integer bannerId,
            @PathVariable String categoryName) {
        try {
            adminBannerService.addProductsByCategoryToBanner(bannerId, categoryName);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{bannerId}/products/brand/{brandName}")
    public ResponseEntity<?> addProductsByBrandToBanner(
            @PathVariable Integer bannerId,
            @PathVariable String brandName) {
        try {
            adminBannerService.addProductsByBrandToBanner(bannerId, brandName);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{bannerId}/products/type/{typeName}")
    public ResponseEntity<?> addProductsByTypeToBanner(
            @PathVariable Integer bannerId,
            @PathVariable String typeName) {
        try {
            adminBannerService.addProductsByTypeToBanner(bannerId, typeName);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private void validateBannerDates(CUBannerRequest request) {
        if (request.getStartDate() != null && request.getEndDate() != null
                && request.getStartDate().after(request.getEndDate())) {
            throw new IllegalArgumentException("Ngày bắt đầu phải trước ngày kết thúc");
        }
    }
}