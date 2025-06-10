package com.petshop.petopia.dto.response.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAllProductResponse {
    private int id;
    private String name;
    private String thumbnailUrl;
    private Double rate;
    private String description;
    private String brandName;
    private BigDecimal percentDiscount;
    private BigDecimal price;
    private BigDecimal priceDiscount;
//    private boolean isWishlist;
}
