package com.petshop.petopia.component;

import com.petshop.petopia.dto.response.review.RatingOverviewResponse;
import com.petshop.petopia.model.review.Rating;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CalculateRating {
    public static int calculateTotalRatings(Rating rating) {
        if (rating == null) {
            return 0;
        }
        return rating.getOneStarCount() +
                rating.getTwoStarCount() +
                rating.getThreeStarCount() +
                rating.getFourStarCount() +
                rating.getFiveStarCount();
    }

    public static double calculateAverageRating(Rating rating) {
        int totalRatings = calculateTotalRatings(rating);
        if (rating == null || totalRatings == 0) {
            return 0.0;
        }
        double sumOfScores = (rating.getOneStarCount() * 1.0) +
                (rating.getTwoStarCount() * 2.0) +
                (rating.getThreeStarCount() * 3.0) +
                (rating.getFourStarCount() * 4.0) +
                (rating.getFiveStarCount() * 5.0);
        return sumOfScores / totalRatings;
    }

    public static void populateStarPercentages(Rating rating,
                                               RatingOverviewResponse.RatingOverviewResponseBuilder builder) {
        int totalRatings = calculateTotalRatings(rating);
        if (totalRatings == 0) {
            builder.oneStarPercentage(0.0)
                    .twoStarPercentage(0.0)
                    .threeStarPercentage(0.0)
                    .fourStarPercentage(0.0)
                    .fiveStarPercentage(0.0);
            return;
        }

        builder.oneStarPercentage(roundToTwoDecimalPlaces((double) rating.getOneStarCount() / totalRatings * 100))
                .twoStarPercentage(roundToTwoDecimalPlaces((double) rating.getTwoStarCount() / totalRatings * 100))
                .threeStarPercentage(roundToTwoDecimalPlaces((double) rating.getThreeStarCount() / totalRatings * 100))
                .fourStarPercentage(roundToTwoDecimalPlaces((double) rating.getFourStarCount() / totalRatings * 100))
                .fiveStarPercentage(roundToTwoDecimalPlaces((double) rating.getFiveStarCount() / totalRatings * 100));
    }

    public static double roundToTwoDecimalPlaces(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
