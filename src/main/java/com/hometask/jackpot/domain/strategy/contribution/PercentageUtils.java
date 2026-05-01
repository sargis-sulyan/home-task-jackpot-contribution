package com.hometask.jackpot.domain.strategy.contribution;

import java.math.BigDecimal;
import java.math.RoundingMode;

final class PercentageUtils {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    private PercentageUtils() {
    }

    static BigDecimal percentageOf(BigDecimal amount, BigDecimal percentage) {
        return amount
                .multiply(percentage)
                .divide(ONE_HUNDRED, 4, RoundingMode.HALF_UP);
    }
}