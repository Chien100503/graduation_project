package com.petshop.petopia.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Integer id;
    private String name;
    private String brandName;
    private String typeName;
    private String description;
    private Integer price;
    private Integer stockQuantity;
    private Integer size;
    private Double weight;
    private String expirationDate;
    private List<String> imageUrls;
    private Date createdAt;
    private Date updatedAt;
    private String productCategoryName;
}
