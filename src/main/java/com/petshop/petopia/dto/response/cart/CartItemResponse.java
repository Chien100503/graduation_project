package com.petshop.petopia.dto.response.cart;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartItemResponse {
    private Integer id;
    private Integer productId;
    private String productName;
    private Integer petId;
    private String petName;
    private Integer quantity;
    private Double price;
    private Double itemTotalPrice;
}
