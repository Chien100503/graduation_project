package com.petshop.petopia.controller.user;

import com.petshop.petopia.dto.request.user.SearchRequest;
import com.petshop.petopia.dto.response.pet.GetAllPetResponse;
import com.petshop.petopia.dto.response.product.GetAllProductResponse;
import com.petshop.petopia.dto.response.user.SearchResponse;
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

    @GetMapping
    public ResponseEntity<SearchResponse> search(
            @ModelAttribute SearchRequest request) {
        SearchResponse response = searchService.searchAll(request);
        return ResponseEntity.ok(response);
    }
}