package com.petshop.petopia.dto.request.product;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateBrandRequest {
    @NotBlank(message = "Tên thương hiệu là bắt buộc")
    private String name;
}