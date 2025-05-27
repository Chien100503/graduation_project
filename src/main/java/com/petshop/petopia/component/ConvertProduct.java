package com.petshop.petopia.component;

import com.petshop.petopia.dto.response.product.ProductCategoryResponse;
import com.petshop.petopia.dto.response.product.ProductResponse;
import com.petshop.petopia.model.product.Product;
import com.petshop.petopia.model.product.ProductImage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConvertProduct {
    public ProductResponse convertToResponse(Product product) {
        List<String> imageUrls = product.getProductImages() != null
                ? product.getProductImages().stream()
                .map(ProductImage::getImageUrl)
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

    public ProductCategoryResponse convertToProductResponse(Product product) {
        return new ProductCategoryResponse(
                product.getId(),
                product.getName(),
                product.getProductImages() != null && !product.getProductImages().isEmpty()
                        ? product.getProductImages().getFirst().getImageUrl() : null
        );
    }
}
