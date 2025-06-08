// File: SearchService.java
package com.petshop.petopia.service.user;

import com.petshop.petopia.component.CalculatePrice;
import com.petshop.petopia.component.CalculateRating;
import com.petshop.petopia.dto.request.user.SearchRequest;
import com.petshop.petopia.dto.response.pet.GetAllPetResponse;
import com.petshop.petopia.dto.response.product.GetAllProductResponse;
import com.petshop.petopia.model.pet.Pet;
import com.petshop.petopia.model.product.Product;
import com.petshop.petopia.repository.pet.PetRepository;
import com.petshop.petopia.repository.product.ProductRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private final PetRepository petRepository;
    private final ProductRepository productRepository;
    private final CalculateRating calculateRating;
    private final CalculatePrice calculatePrice;

    public List<GetAllPetResponse> searchPets(SearchRequest request) {
        return petRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Tìm kiếm theo từ khóa trong tên hoặc mô tả
            if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
                String keywordLike = "%" + request.getKeyword().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), keywordLike),
                        cb.like(cb.lower(root.get("description")), keywordLike)
                ));
            }

            predicates.add(cb.isTrue(root.get("status")));

            return cb.and(predicates.toArray(new Predicate[0]));
        }).stream().map(this::toPetDto).toList();
    }

    public List<GetAllProductResponse> searchProducts(SearchRequest request) {
        return productRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
                String keywordLike = "%" + request.getKeyword().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), keywordLike),
                        cb.like(cb.lower(root.get("description")), keywordLike)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }).stream().map(this::toProductDto).toList();
    }

    private GetAllPetResponse toPetDto(Pet pet) {
        return new GetAllPetResponse(
                pet.getId(),
                pet.getName(),
                pet.getThumbnail(),
                pet.getBanner().getSalePercent(),
                pet.getPrice(),
                calculatePrice.calculatePriceDiscount(pet.getPrice(), pet.getBanner().getSalePercent())
        );
    }

    private GetAllProductResponse toProductDto(Product product) {
        double rawAverageRating = CalculateRating.calculateAverageRating(product.getRating());
        double roundedAverageRating = CalculateRating.roundToTwoDecimalPlaces(rawAverageRating);

        return new GetAllProductResponse(
                product.getId(),
                product.getName(),
                product.getThumbnail(),
                roundedAverageRating,
                product.getBanner().getSalePercent(),
                product.getPrice(),
                calculatePrice.calculatePriceDiscount(product.getPrice(), product.getBanner().getSalePercent())
        );
    }
}