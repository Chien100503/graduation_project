package com.petshop.petopia.dto.response.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetProductDetailResponse {
    private int id;
    private String name;
    private String categoryName;
    private String typeName;
    private String description;
    private Double rate;
    private BigDecimal percentDiscount;
    private BigDecimal price;
    private BigDecimal priceDiscount;
    private List<String> imageUrl;
    private int stockQuantity;
    private String size;
    private Double weight;
    private String expirationDate;
    private boolean isWishlist;
}
