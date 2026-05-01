package com.hometask.jackpot.domain.strategy.contribution;

import java.math.BigDecimal;

public record ContributionCalculationContext(
        BigDecimal betAmount,
        BigDecimal initialPoolAmount,
        BigDecimal currentPoolAmount,
        BigDecimal fixedContributionPercentage,
        BigDecimal variableContributionInitialPercentage,
        BigDecimal variableContributionMinPercentage,
        BigDecimal variableContributionDecreasePerStep,
        BigDecimal variableContributionStepAmount
) {
}