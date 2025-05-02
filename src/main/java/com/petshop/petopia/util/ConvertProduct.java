package com.petshop.petopia.util;

import com.petshop.petopia.dto.response.ProductResponse;
import com.petshop.petopia.model.product.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConvertProduct {
    public ProductResponse convertToResponse(Product product) {
        List<String> imageUrls = product.getProductImages() != null
                ? product.getProductImages().stream()
                .map(productImage -> productImage.getImageUrl()) // Corrected line
                .collect(Collectors.toList())
                : List.of();

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getBrand() != null ? product.getBrand().getName() : null,
                product.getType() != null ? product.getType().getName() : null,
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getSize(),
                product.getWeight(),
                product.getExpirationDate(),
                imageUrls,
                product.getCreatedAt(),
                product.getUpdatedAt(),
                product.getPrCategory() != null ? product.getPrCategory().getName() : null
        );
    }
}
