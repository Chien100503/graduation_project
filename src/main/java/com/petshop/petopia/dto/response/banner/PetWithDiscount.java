package com.petshop.petopia.dto.response.banner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PetWithDiscount {
    private Integer id;
    private String thumbnail;
    private String name;
    private BigDecimal originalPrice;
    private BigDecimal discountedPrice;
}
