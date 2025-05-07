package com.petshop.petopia.dto.response.pet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetPetResponse {
    private Integer id;
    private String petCategoryName;
    private String breedName;
    private String name;
    private Integer age;
    private String gender;
    private String size;
    private Double weight;
    private String color;
    private Integer price;
    private Boolean status;
    private String description;
    private List<String> imageUrls;
}
