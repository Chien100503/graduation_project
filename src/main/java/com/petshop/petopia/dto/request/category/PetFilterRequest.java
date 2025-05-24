package com.petshop.petopia.dto.request.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetFilterRequest {
    private String category;
    private String breed;

    public void validate() {
        if ((category == null || category.isBlank()) &&
                (breed == null || breed.isBlank())) {
            throw new IllegalArgumentException("Ít nhất một tiêu chí lọc (category hoặc breed) phải được cung cấp");
        }
    }
}
