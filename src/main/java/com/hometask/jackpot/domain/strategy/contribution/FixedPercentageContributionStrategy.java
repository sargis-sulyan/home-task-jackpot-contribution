package com.hometask.jackpot.domain.strategy.contribution;

import com.hometask.jackpot.domain.model.ContributionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FixedPercentageContributionStrategy implements ContributionStrategy {

    @Override
    public ContributionType type() {
        return ContributionType.FIXED_PERCENTAGE;
    }

    @Override
    public BigDecimal calculate(ContributionCalculationContext context) {
        return PercentageUtils.percentageOf(
                context.betAmount(),
                context.fixedContributionPercentage()
        );
    }
}