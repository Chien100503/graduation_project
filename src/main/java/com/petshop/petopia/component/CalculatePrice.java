package com.petshop.petopia.component;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CalculatePrice {
    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final int DECIMAL_PLACES = 2;

    public BigDecimal getPercentDiscount(BigDecimal salePercent) {
        if (salePercent == null || salePercent.compareTo(BigDecimal.ZERO) < 0 || salePercent.compareTo(HUNDRED) > 0) {
            return BigDecimal.ZERO;
        }
        return salePercent;
    }

    public BigDecimal calculatePriceDiscount(BigDecimal originalPrice, BigDecimal percentDiscount) {
        if (originalPrice == null || percentDiscount == null || percentDiscount.compareTo(BigDecimal.ZERO) < 0 || percentDiscount.compareTo(HUNDRED) > 0) {
            return originalPrice;
        }

        BigDecimal discountValue = originalPrice.multiply(percentDiscount)
                .divide(HUNDRED, DECIMAL_PLACES, RoundingMode.HALF_UP);

        return originalPrice.subtract(discountValue);
    }

    public BigDecimal roundBigDecimal(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return value.setScale(DECIMAL_PLACES, RoundingMode.HALF_UP);
    }
}