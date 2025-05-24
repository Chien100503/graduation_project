package com.petshop.petopia.controller.admin;

import com.petshop.petopia.dto.request.banner.CUBannerRequest;
import com.petshop.petopia.dto.response.banner.*;
import com.petshop.petopia.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin/banner")
@RequiredArgsConstructor
public class AdminBannerController {

    private final BannerService bannerService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createBanner(@ModelAttribute CUBannerRequest request) {
        try {
            validateBannerDates(request);
            CUBannerResponse response = bannerService.createBanner(request);
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
            CUBannerResponse response = bannerService.updateBanner(id, request);
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
            bannerService.deleteBanner(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // Pet endpoints
    @PostMapping("/{bannerId}/pets/{petId}")
    public ResponseEntity<?> addPetToBanner(
            @PathVariable Integer bannerId,
            @PathVariable Integer petId) {
        try {
            bannerService.addPetToBanner(bannerId, petId);
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
            bannerService.removePetFromBanner(petId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // Product endpoints
    @PostMapping("/{bannerId}/products/{productId}")
    public ResponseEntity<?> addProductToBanner(
            @PathVariable Integer bannerId,
            @PathVariable Integer productId) {
        try {
            bannerService.addProductToBanner(bannerId, productId);
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
            bannerService.removeProductFromBanner(productId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // Bulk operations
    @PostMapping("/{bannerId}/pets")
    public ResponseEntity<?> addAllPetsToBanner(@PathVariable Integer bannerId) {
        try {
            bannerService.addAllPetsToBanner(bannerId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{bannerId}/products")
    public ResponseEntity<?> addAllProductsToBanner(@PathVariable Integer bannerId) {
        try {
            bannerService.addAllProductsToBanner(bannerId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Category/type operations
    @PostMapping("/{bannerId}/pets/category/{categoryName}")
    public ResponseEntity<?> addPetsByCategoryToBanner(
            @PathVariable Integer bannerId,
            @PathVariable String categoryName) {
        try {
            bannerService.addPetsByCategoryToBanner(bannerId, categoryName);
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
            bannerService.addPetsByBreedToBanner(bannerId, breedName);
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
            bannerService.addProductsByCategoryToBanner(bannerId, categoryName);
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
            bannerService.addProductsByBrandToBanner(bannerId, brandName);
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
            bannerService.addProductsByTypeToBanner(bannerId, typeName);
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