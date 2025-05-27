package com.petshop.petopia.dto.response.pet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAllPetResponse {
    private Integer id;
    private String name;
    private BigDecimal price;
    private String imageUrl;
}
