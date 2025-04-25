package com.petshop.petopia.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class PetResponse {
    private Integer pid;
    private String name;
    private String breed;
    private Integer age;
    private String gender;
    private String size;
    private Double weight;
    private String color;
    private Double price;
    private String status;         // "Available" hoặc "Ordered"
    private Boolean healthStatus;
    private String description;
    private String img;
    private Date createdAt;
    private Date updatedAt;
    private String categoryName;  // Optional: tên của ProductCategory (nếu cần hiển thị)
}
