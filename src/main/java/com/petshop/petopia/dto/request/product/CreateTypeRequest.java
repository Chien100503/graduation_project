package com.petshop.petopia.dto.request.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTypeRequest {
    @NotBlank(message = "Tên loại là bắt buộc")
    private String name;

    @NotNull(message = "Tên danh mục sản phẩm là bắt buộc")
    private String productCategoryName;
}
