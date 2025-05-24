package com.petshop.petopia.component;

import com.petshop.petopia.dto.response.banner.PetWithDiscount;
import com.petshop.petopia.dto.response.banner.ProductWithDiscount;
import com.petshop.petopia.model.pet.Pet;
import com.petshop.petopia.model.product.Product;
import org.springframework.stereotype.Component;

@Component
public class CalculateDiscount {

    public int calculatePetDiscount(int originalPrice, Double salePercent) {
        return salePercent != null ?
                (int) (originalPrice * (1 - salePercent)) :
                originalPrice;
    }

    public int calculateProductDiscount(int originalPrice, Double salePercent) {
        return salePercent != null ?
                (int) (originalPrice * (1 - salePercent)) :
                originalPrice;
    }
}