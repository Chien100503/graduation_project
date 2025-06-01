package com.petshop.petopia.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePetResponse {
    private Integer id;
    private String name;
    private String breedName;
    private Integer age;
    private String gender;
    private String size;
    private Double weight;
    private String color;
    private BigDecimal price;
    private String status;
    private String description;
    private String thumnail;
    private List<String> imageUrls;
    private Date createdAt;
    private Date updatedAt;
    private String petCategoryName;
}
