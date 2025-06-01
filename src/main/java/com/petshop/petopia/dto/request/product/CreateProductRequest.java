package com.petshop.petopia.dto.request.product;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateProductRequest {
    private List<MultipartFile> file;
    private MultipartFile thumbnail;
    private String productCategoryName;
    private String name;
    private String brandName;
    private String typeName;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String size;
    private Double weight;
    private String expirationDate;
}