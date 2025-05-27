package com.petshop.petopia.component;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CalculateDiscount {

    public BigDecimal calculatePetDiscount(BigDecimal originalPrice, BigDecimal salePercent) {
        if (salePercent == null) {
            return originalPrice;
        }
        BigDecimal discountMultiplier = BigDecimal.ONE.subtract(salePercent);

        return originalPrice.multiply(discountMultiplier)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateProductDiscount(BigDecimal originalPrice, BigDecimal salePercent) {
        if (salePercent == null) {
            return originalPrice;
        }

        BigDecimal discountMultiplier = BigDecimal.ONE.subtract(salePercent);

        return originalPrice.multiply(discountMultiplier)
                .setScale(2, RoundingMode.HALF_UP);
    }
}