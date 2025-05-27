package com.petshop.petopia.dto.response.review;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewResponse {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private String comment;
    private LocalDateTime createdAt;
}