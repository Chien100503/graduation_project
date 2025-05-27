package com.petshop.petopia.dto.request.review;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReviewRequest {
    @NotBlank(message = "Bình luận không được để trống")
    private String comment;
}