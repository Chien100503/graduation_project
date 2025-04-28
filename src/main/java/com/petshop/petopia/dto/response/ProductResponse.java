package com.petshop.petopia.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class ProductResponse {
    private Integer id;
    private String name;
    private String brand;
    private String type;
    private String description;
    private Double price;
    private Integer stockQuantity;
    private Integer size;
    private Double weight;
    private Date expirationDate;
    private String images;
    private Date createdAt;
    private Date updatedAt;
    private String category;
}