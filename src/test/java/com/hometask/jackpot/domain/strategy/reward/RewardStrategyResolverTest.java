package com.hometask.jackpot.domain.strategy.reward;

import com.hometask.jackpot.domain.exception.UnsupportedJackpotConfigurationException;
import com.hometask.jackpot.domain.model.RewardType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RewardStrategyResolverTest {

    @Test
    void shouldResolveStrategyByRewardType() {
        // Given
        RewardStrategy fixedStrategy = strategy(RewardType.FIXED_CHANCE);
        RewardStrategy variableStrategy = strategy(RewardType.VARIABLE_CHANCE);
        RewardStrategyResolver resolver = new RewardStrategyResolver(List.of(
                fixedStrategy,
                variableStrategy
        ));

        // When
        RewardStrategy resolvedStrategy = resolver.resolve(RewardType.VARIABLE_CHANCE);

        // Then
        assertThat(resolvedStrategy).isSameAs(variableStrategy);
    }

    @Test
    void shouldThrowWhenRewardTypeIsMissing() {
        // Given
        RewardStrategyResolver resolver = new RewardStrategyResolver(List.of());

        // When / Then
        assertThatThrownBy(() -> resolver.resolve(RewardType.FIXED_CHANCE))
                .isInstanceOf(UnsupportedJackpotConfigurationException.class)
                .hasMessageContaining("Unsupported reward type");
    }

    @Test
    void shouldThrowWhenDuplicateStrategiesForSameTypeAreProvided() {
        // Given
        RewardStrategy firstStrategy = strategy(RewardType.FIXED_CHANCE);
        RewardStrategy secondStrategy = strategy(RewardType.FIXED_CHANCE);

        // When / Then
        assertThatThrownBy(() -> new RewardStrategyResolver(List.of(firstStrategy, secondStrategy)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Duplicate reward strategy");
    }

    private RewardStrategy strategy(RewardType type) {
        return new RewardStrategy() {
            @Override
            public RewardType type() {
                return type;
            }

            @Override
            public RewardCalculationResult evaluate(RewardCalculationContext context) {
                return new RewardCalculationResult(false, BigDecimal.ZERO, BigDecimal.ZERO);
            }
        };
    }
}
