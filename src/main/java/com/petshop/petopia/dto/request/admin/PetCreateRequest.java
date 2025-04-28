package com.petshop.petopia.dto.request.admin;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PetCreateRequest {
    private MultipartFile file;
    private String petCategoryName;
    private String name;
    private String breed;
    private Integer age;
    private String gender;
    private String size;
    private Double weight;
    private String color;
    private Double price;
    private String status;
    private Boolean healthStatus;
    private String description;
}
