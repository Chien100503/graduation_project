package com.petshop.petopia.dto.request.pet;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateBreedRequest {
    @NotBlank(message = "Tên giống là bắt buộc")
    private String name;

    @NotNull(message = "Name Category danh mục là bắt buộc")
    private String categoryName;
}
