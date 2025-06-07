package com.petshop.petopia.dto.response;

import com.petshop.petopia.dto.response.pet.GetAllPetResponse;
import com.petshop.petopia.dto.response.product.GetAllProductResponse;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class SearchResponse {
    private Page<GetAllPetResponse> pets;
    private Page<GetAllProductResponse> products;
}
