package com.petshop.petopia.dto.response.banner;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProductWithDiscount {
    private Integer id;
    private String thumnail;
    private String name;
    private BigDecimal originalPrice;
    private BigDecimal discountedPrice;
}
