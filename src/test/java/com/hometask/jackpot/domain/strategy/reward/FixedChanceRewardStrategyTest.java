package com.hometask.jackpot.domain.strategy.reward;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FixedChanceRewardStrategyTest {

    private final RandomPercentageGenerator randomPercentageGenerator = mock(RandomPercentageGenerator.class);
    private final FixedChanceRewardStrategy strategy = new FixedChanceRewardStrategy(randomPercentageGenerator);

    @Test
    void shouldWinWhenRandomPercentageIsLowerThanFixedChance() {
        // Given
        when(randomPercentageGenerator.nextPercentage()).thenReturn(new BigDecimal("9.99"));

        // When
        RewardCalculationResult result = strategy.evaluate(context(new BigDecimal("10")));

        // Then
        assertThat(result.won()).isTrue();
        assertThat(result.rewardAmount()).isEqualByComparingTo("1250.0000");
        assertThat(result.winningChancePercentage()).isEqualByComparingTo("10");
    }

    @Test
    void shouldLoseWhenRandomPercentageEqualsFixedChance() {
        // Given
        when(randomPercentageGenerator.nextPercentage()).thenReturn(new BigDecimal("10"));

        // When
        RewardCalculationResult result = strategy.evaluate(context(new BigDecimal("10")));

        // Then
        assertThat(result.won()).isFalse();
        assertThat(result.rewardAmount()).isEqualByComparingTo("0");
    }

    @Test
    void shouldLoseWhenRandomPercentageIsGreaterThanFixedChance() {
        // Given
        when(randomPercentageGenerator.nextPercentage()).thenReturn(new BigDecimal("10.01"));

        // When
        RewardCalculationResult result = strategy.evaluate(context(new BigDecimal("10")));

        // Then
        assertThat(result.won()).isFalse();
        assertThat(result.rewardAmount()).isEqualByComparingTo("0");
    }

    private RewardCalculationContext context(BigDecimal fixedChancePercentage) {
        return new RewardCalculationContext(
                new BigDecimal("1000.0000"),
                new BigDecimal("1250.0000"),
                fixedChancePercentage,
                null,
                null,
                null,
                null
        );
    }
}
