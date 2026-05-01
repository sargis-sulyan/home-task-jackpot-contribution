package com.hometask.jackpot.domain.strategy.contribution;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class VariablePercentageContributionStrategyTest {

    private final VariablePercentageContributionStrategy strategy = new VariablePercentageContributionStrategy();

    @Test
    void shouldUseInitialPercentageWhenCurrentPoolEqualsInitialPool() {
        // Given
        ContributionCalculationContext context = context(
                new BigDecimal("100.00"),
                new BigDecimal("1000.00"),
                new BigDecimal("1000.00")
        );

        // When
        BigDecimal contributionAmount = strategy.calculate(context);

        // Then
        assertThat(contributionAmount).isEqualByComparingTo("10.0000");
    }

    @Test
    void shouldDecreasePercentageWhenPoolGrowsByConfiguredStep() {
        // Given
        ContributionCalculationContext context = context(
                new BigDecimal("100.00"),
                new BigDecimal("1000.00"),
                new BigDecimal("1500.00")
        );

        // When
        BigDecimal contributionAmount = strategy.calculate(context);

        // Then
        assertThat(contributionAmount).isEqualByComparingTo("9.0000");
    }

    @Test
    void shouldNeverUsePercentageBelowMinimum() {
        // Given
        ContributionCalculationContext context = context(
                new BigDecimal("100.00"),
                new BigDecimal("1000.00"),
                new BigDecimal("10000.00")
        );

        // When
        BigDecimal contributionAmount = strategy.calculate(context);

        // Then
        assertThat(contributionAmount).isEqualByComparingTo("3.0000");
    }

    private ContributionCalculationContext context(
            BigDecimal betAmount,
            BigDecimal initialPoolAmount,
            BigDecimal currentPoolAmount
    ) {
        return new ContributionCalculationContext(
                betAmount,
                initialPoolAmount,
                currentPoolAmount,
                null,
                new BigDecimal("10"),
                new BigDecimal("3"),
                new BigDecimal("1"),
                new BigDecimal("500")
        );
    }
}
