package com.petshop.petopia.dto.response.pet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBreedResponse {
    private Integer id;
    private String name;
    private String categoryName;
}