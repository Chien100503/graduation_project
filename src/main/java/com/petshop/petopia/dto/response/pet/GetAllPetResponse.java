package com.petshop.petopia.dto.response.pet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAllPetResponse {
    private int id;
    private String name;
    private String thumbnailUrl;
    private String description;
    private String breedName;
    private BigDecimal percentDiscount;
    private BigDecimal price;
    private BigDecimal priceDiscount;
    private boolean isWishlist;
}
