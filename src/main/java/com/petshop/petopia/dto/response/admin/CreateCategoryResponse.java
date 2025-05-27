package com.petshop.petopia.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCategoryResponse {
    private Integer id;
    private String name;
    private String imageUrl;
}
