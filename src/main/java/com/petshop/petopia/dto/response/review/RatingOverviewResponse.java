package com.petshop.petopia.dto.response.review;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatingOverviewResponse {
    private Integer productId;
    private Double oneStarPercentage;
    private Double twoStarPercentage;
    private Double threeStarPercentage;
    private Double fourStarPercentage;
    private Double fiveStarPercentage;
    private Integer totalRatings;
    private Double averageRating;
}