package com.petshop.petopia.dto.request.admin;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateCategoryRequest {
    private String name;
    private MultipartFile file;
}
