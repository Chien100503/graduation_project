package com.petshop.petopia.controller.user;

import com.petshop.petopia.dto.request.user.SearchRequest;
import com.petshop.petopia.dto.response.pet.GetAllPetResponse;
import com.petshop.petopia.dto.response.product.GetAllProductResponse;
import com.petshop.petopia.service.user.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    // Đã thay đổi Pageable thành List và bỏ tham số Pageable
    @GetMapping("/products")
    public ResponseEntity<List<GetAllProductResponse>> searchProducts(
            @ModelAttribute SearchRequest request) { // Bỏ tham số Pageable
        List<GetAllProductResponse> products = searchService.searchProducts(request);
        return ResponseEntity.ok(products);
    }

    // Đã thay đổi Pageable thành List và bỏ tham số Pageable
    @GetMapping("/pets")
    public ResponseEntity<List<GetAllPetResponse>> searchPets(
            @ModelAttribute SearchRequest request) { // Bỏ tham số Pageable
        List<GetAllPetResponse> pets = searchService.searchPets(request);
        return ResponseEntity.ok(pets);
    }
}