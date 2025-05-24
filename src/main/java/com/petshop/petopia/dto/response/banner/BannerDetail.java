package com.petshop.petopia.dto.response.banner;

import com.petshop.petopia.model.pet.Pet;
import com.petshop.petopia.model.sale.Banner;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BannerDetail {
    private Banner banner;
    List<ProductWithDiscount> products;
    List<PetWithDiscount> pets;
}
