package com.hometask.jackpot.domain.strategy.contribution;

import com.hometask.jackpot.domain.model.ContributionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class VariablePercentageContributionStrategy implements ContributionStrategy {

    @Override
    public ContributionType type() {
        return ContributionType.VARIABLE_PERCENTAGE;
    }

    @Override
    public BigDecimal calculate(ContributionCalculationContext context) {
        BigDecimal poolGrowth = context.currentPoolAmount()
                .subtract(context.initialPoolAmount())
                .max(BigDecimal.ZERO);

        BigDecimal steps = poolGrowth.divide(
                context.variableContributionStepAmount(),
                0,
                RoundingMode.DOWN
        );

        BigDecimal decrease = steps.multiply(context.variableContributionDecreasePerStep());

        BigDecimal calculatedPercentage = context.variableContributionInitialPercentage()
                .subtract(decrease);

        BigDecimal finalPercentage = calculatedPercentage.max(
                context.variableContributionMinPercentage()
        );

        return PercentageUtils.percentageOf(context.betAmount(), finalPercentage);
    }
}
