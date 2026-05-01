package com.hometask.jackpot.domain.strategy.reward;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VariableChanceRewardStrategyTest {

    private final RandomPercentageGenerator randomPercentageGenerator = mock(RandomPercentageGenerator.class);
    private final VariableChanceRewardStrategy strategy = new VariableChanceRewardStrategy(randomPercentageGenerator);

    @Test
    void shouldUseInitialChanceWhenCurrentPoolEqualsInitialPool() {
        // Given
        when(randomPercentageGenerator.nextPercentage()).thenReturn(new BigDecimal("5.00"));

        // When
        RewardCalculationResult result = strategy.evaluate(context(
                new BigDecimal("1000.0000"),
                new BigDecimal("1000.0000")
        ));

        // Then
        assertThat(result.won()).isFalse();
        assertThat(result.rewardAmount()).isEqualByComparingTo("0");
        assertThat(result.winningChancePercentage()).isEqualByComparingTo("5");
    }

    @Test
    void shouldIncreaseChanceWhenPoolGrowsByConfiguredSteps() {
        // Given
        when(randomPercentageGenerator.nextPercentage()).thenReturn(new BigDecimal("8.99"));

        // When
        RewardCalculationResult result = strategy.evaluate(context(
                new BigDecimal("1000.0000"),
                new BigDecimal("2000.0000")
        ));

        // Then
        assertThat(result.won()).isTrue();
        assertThat(result.rewardAmount()).isEqualByComparingTo("2000.0000");
        assertThat(result.winningChancePercentage()).isEqualByComparingTo("9");
    }

    @Test
    void shouldUseGuaranteedChanceWhenCurrentPoolReachesGuaranteedAmount() {
        // Given
        when(randomPercentageGenerator.nextPercentage()).thenReturn(new BigDecimal("99.99"));

        // When
        RewardCalculationResult result = strategy.evaluate(context(
                new BigDecimal("1000.0000"),
                new BigDecimal("5000.0000")
        ));

        // Then
        assertThat(result.won()).isTrue();
        assertThat(result.rewardAmount()).isEqualByComparingTo("5000.0000");
        assertThat(result.winningChancePercentage()).isEqualByComparingTo("100");
    }

    @Test
    void shouldLoseDeterministicallyWhenRandomPercentageIsNotLowerThanChance() {
        // Given
        when(randomPercentageGenerator.nextPercentage()).thenReturn(new BigDecimal("9.00"));

        // When
        RewardCalculationResult result = strategy.evaluate(context(
                new BigDecimal("1000.0000"),
                new BigDecimal("2000.0000")
        ));

        // Then
        assertThat(result.won()).isFalse();
        assertThat(result.rewardAmount()).isEqualByComparingTo("0");
        assertThat(result.winningChancePercentage()).isEqualByComparingTo("9");
    }

    private RewardCalculationContext context(
            BigDecimal initialPoolAmount,
            BigDecimal currentPoolAmount
    ) {
        return new RewardCalculationContext(
                initialPoolAmount,
                currentPoolAmount,
                null,
                new BigDecimal("5"),
                new BigDecimal("2"),
                new BigDecimal("500"),
                new BigDecimal("5000")
        );
    }
}
