package com.petshop.petopia.dto.response.banner;

import com.fasterxml.jackson.core.JsonToken;
import com.petshop.petopia.model.product.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductWithDiscount {
    private Integer id;
    private String imageUrl;
    private String name;
    private Integer originalPrice;
    private Integer discountedPrice;
}
