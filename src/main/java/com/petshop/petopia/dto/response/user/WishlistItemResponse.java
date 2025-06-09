package com.petshop.petopia.dto.response.user;

import com.petshop.petopia.dto.response.pet.GetAllPetResponse;
import com.petshop.petopia.dto.response.product.GetAllProductResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItemResponse {
    private String type;
    private GetAllPetResponse pet;
    private GetAllProductResponse product;
}