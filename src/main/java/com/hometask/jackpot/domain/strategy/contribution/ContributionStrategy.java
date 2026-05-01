package com.hometask.jackpot.domain.strategy.contribution;

import com.hometask.jackpot.domain.model.ContributionType;

import java.math.BigDecimal;

public interface ContributionStrategy {

    ContributionType type();

    BigDecimal calculate(ContributionCalculationContext context);
}