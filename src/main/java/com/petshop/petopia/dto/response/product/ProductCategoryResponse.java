package com.petshop.petopia.dto.response.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryResponse {
    private Integer id;
    private String name;
    private String imageUrl;
}
