package com.petshop.petopia.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {
    private String keyword;
    private Integer petCategoryId;
    private Integer productCategoryId;
    private Integer brandId;
    private Integer typeId;
    private Integer breedId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
}