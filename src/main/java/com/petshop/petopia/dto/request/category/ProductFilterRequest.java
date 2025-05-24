package com.petshop.petopia.dto.request.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilterRequest {
    private String category;
    private String brand;
    private String type;

    // Validate request
    public void validate() {
        if ((category == null || category.isBlank()) &&
                (brand == null || brand.isBlank()) &&
                (type == null || type.isBlank())) {
            throw new IllegalArgumentException("Ít nhất một tiêu chí lọc phải được cung cấp");
        }
    }
}