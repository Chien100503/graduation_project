package com.petshop.petopia.dto.response.cart;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.petshop.petopia.component.Global;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartItemResponse {
    private Integer id;
    private Integer productId;
    private String productName;
    private Integer petId;
    private String petName;
    private String thumbnailUrl;
    private Global.ItemType itemType;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal priceDiscount;
    private BigDecimal itemTotalPrice;
    private String breedName;
    private String brandName;
}
