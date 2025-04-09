package com.petshop.petopia.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PetResponse {
    private int id;
    private String name;
    private String imgUrl;
}
