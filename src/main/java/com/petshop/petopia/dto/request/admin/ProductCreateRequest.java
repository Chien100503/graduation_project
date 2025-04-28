package com.petshop.petopia.dto.request.admin;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
public class ProductCreateRequest {
    private MultipartFile file;
    private String name;
    private String productCategoryName;
    private String brand;
    private String type;
    private String description;
    private Integer stockQuantity;
    private Integer size;
    private Double weight;
    private Date expirationDate;
    private Double price;
    private Boolean healthStatus; // Vẫn giữ, nhưng bạn nên cân nhắc lại sự phù hợp
}