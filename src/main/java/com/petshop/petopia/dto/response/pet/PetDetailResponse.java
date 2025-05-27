package com.petshop.petopia.dto.response.pet;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetDetailResponse {
    private Integer id;
    private String petCategoryName;
    private String breedName;
    private String name;
    private Integer age;
    private String gender;
    private String size;
    private Double weight;
    private String color;
    private BigDecimal price;
    private Boolean status;
    private String description;
    private List<String> imageUrls;
}