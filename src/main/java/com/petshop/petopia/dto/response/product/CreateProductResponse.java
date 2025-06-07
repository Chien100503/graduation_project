package com.petshop.petopia.dto.response.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductResponse {
    private Integer id;
    private String name;
    private String categoryName;
    private String brandName;
    private String typeName;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String size;
    private Double weight;
    private String expirationDate;
    private List<String> imageUrl;
    private String thumbnailUrl;
}
