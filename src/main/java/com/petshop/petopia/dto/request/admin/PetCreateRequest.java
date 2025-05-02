package com.petshop.petopia.dto.request.admin;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class PetCreateRequest {
    private List<MultipartFile> file;
    private String petCategoryName;
    private String name;
    private String breedName;
    private Integer age;
    private String gender;
    private String size;
    private Double weight;
    private String color;
    private Integer price;
    private String status;
    private String description;
}