package com.petshop.petopia.dto.request.admin;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ProductCreateRequest {
    private List<MultipartFile> file;
    private String productCategoryName;
    private String name;
    private String brandName;
    private String typeName;
    private String description;
    private Integer price;
    private Integer stockQuantity;
    private Integer size;
    private Double weight;
    private String expirationDate;
}