package com.petshop.petopia.service.user;

import com.petshop.petopia.component.ConvertPet;
import com.petshop.petopia.component.ConvertProduct;
import com.petshop.petopia.dto.request.user.SearchRequest;
import com.petshop.petopia.dto.response.user.SearchResponse;
import com.petshop.petopia.dto.response.pet.GetAllPetResponse;
import com.petshop.petopia.dto.response.product.GetAllProductResponse;
import com.petshop.petopia.model.pet.Pet;
import com.petshop.petopia.model.product.Product;
import com.petshop.petopia.repository.pet.PetRepository;
import com.petshop.petopia.repository.product.ProductRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final PetRepository petRepository;
    private final ProductRepository productRepository;
    private final ConvertPet convertPet;
    private final ConvertProduct convertProduct;

    public SearchResponse searchAll(SearchRequest request) {
        String keyword = request.getKeyword();
        String keywordLike = (keyword == null || keyword.isBlank()) ? null : "%" + keyword.toLowerCase() + "%";

        List<Pet> pets = petRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keywordLike != null) {
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), keywordLike),
                        cb.like(cb.lower(root.join("breed").get("name")), keywordLike),
                        cb.like(cb.lower(root.join("petCategory").get("name")), keywordLike)
                ));
            }
            predicates.add(cb.isTrue(root.get("status")));  // Chỉ lấy pet status = true (Available)
            return cb.and(predicates.toArray(new Predicate[0]));
        });

        List<Product> products = productRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keywordLike != null) {
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), keywordLike),
                        cb.like(cb.lower(root.join("brand").get("name")), keywordLike),
                        cb.like(cb.lower(root.join("prCategory").get("name")), keywordLike),
                        cb.like(cb.lower(root.join("type").get("name")), keywordLike)
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        });

        List<GetAllPetResponse> petResponses = pets.stream()
                .map(convertPet::convertToGetAllPetResponse)
                .toList();

        List<GetAllProductResponse> productResponses = products.stream()
                .map(convertProduct::convertToGetProductResponse)
                .toList();

        return new SearchResponse(petResponses, productResponses);
    }
}
