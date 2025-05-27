package com.petshop.petopia.dto.request.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RatingRequest {
    @NotNull(message = "Giá trị đánh giá không được null")
    @Min(value = 1, message = "Giá trị đánh giá phải từ 1 đến 5")
    @Max(value = 5, message = "Giá trị đánh giá phải từ 1 đến 5")
    private Integer ratingValue;
}