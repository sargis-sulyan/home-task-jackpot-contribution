package com.hometask.jackpot.domain.strategy.contribution;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class FixedPercentageContributionStrategyTest {

    private final FixedPercentageContributionStrategy strategy = new FixedPercentageContributionStrategy();

    @Test
    void shouldCalculateFixedContributionAmount() {
        // Given
        ContributionCalculationContext context = context(
                new BigDecimal("100.00"),
                new BigDecimal("5")
        );

        // When
        BigDecimal contributionAmount = strategy.calculate(context);

        // Then
        assertThat(contributionAmount).isEqualByComparingTo("5.0000");
    }

    @Test
    void shouldCalculateFixedContributionAmountWithFractionalPercentage() {
        // Given
        ContributionCalculationContext context = context(
                new BigDecimal("250.00"),
                new BigDecimal("2.5")
        );

        // When
        BigDecimal contributionAmount = strategy.calculate(context);

        // Then
        assertThat(contributionAmount).isEqualByComparingTo("6.2500");
    }

    private ContributionCalculationContext context(BigDecimal betAmount, BigDecimal fixedPercentage) {
        return new ContributionCalculationContext(
                betAmount,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                fixedPercentage,
                null,
                null,
                null,
                null
        );
    }
}
