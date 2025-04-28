package com.petshop.petopia.util;

import com.petshop.petopia.dto.response.ProductResponse;
import com.petshop.petopia.model.product.Product;
import org.springframework.stereotype.Component;

@Component
public class ConvertProduct {
    public ProductResponse convertToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getBrand(),
                product.getType(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getSize(),
                product.getWeight(),
                product.getExpirationDate(),
                product.getImages(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                product.getPrCategory() != null ? product.getPrCategory().getName() : null
        );
    }
}