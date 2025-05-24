package com.petshop.petopia.dto.response.banner;

import com.petshop.petopia.model.pet.Pet;
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
    private String avatarUrl;
    private String name;
    private Integer originalPrice;
    private Integer discountedPrice;
}
