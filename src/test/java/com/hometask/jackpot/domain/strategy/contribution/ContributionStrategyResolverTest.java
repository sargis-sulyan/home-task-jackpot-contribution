package com.hometask.jackpot.domain.strategy.contribution;

import com.hometask.jackpot.domain.exception.UnsupportedJackpotConfigurationException;
import com.hometask.jackpot.domain.model.ContributionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ContributionStrategyResolverTest {

    @Test
    void shouldResolveStrategyByContributionType() {
        // Given
        ContributionStrategy fixedStrategy = strategy(ContributionType.FIXED_PERCENTAGE);
        ContributionStrategy variableStrategy = strategy(ContributionType.VARIABLE_PERCENTAGE);
        ContributionStrategyResolver resolver = new ContributionStrategyResolver(List.of(
                fixedStrategy,
                variableStrategy
        ));

        // When
        ContributionStrategy resolvedStrategy = resolver.resolve(ContributionType.FIXED_PERCENTAGE);

        // Then
        assertThat(resolvedStrategy).isSameAs(fixedStrategy);
    }

    @Test
    void shouldThrowWhenContributionTypeIsMissing() {
        // Given
        ContributionStrategyResolver resolver = new ContributionStrategyResolver(List.of());

        // When / Then
        assertThatThrownBy(() -> resolver.resolve(ContributionType.FIXED_PERCENTAGE))
                .isInstanceOf(UnsupportedJackpotConfigurationException.class)
                .hasMessageContaining("Unsupported contribution type");
    }

    @Test
    void shouldThrowWhenDuplicateStrategiesForSameTypeAreProvided() {
        // Given
        ContributionStrategy firstStrategy = strategy(ContributionType.FIXED_PERCENTAGE);
        ContributionStrategy secondStrategy = strategy(ContributionType.FIXED_PERCENTAGE);

        // When / Then
        assertThatThrownBy(() -> new ContributionStrategyResolver(List.of(firstStrategy, secondStrategy)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Duplicate contribution strategy");
    }

    private ContributionStrategy strategy(ContributionType type) {
        return new ContributionStrategy() {
            @Override
            public ContributionType type() {
                return type;
            }

            @Override
            public BigDecimal calculate(ContributionCalculationContext context) {
                return BigDecimal.ZERO;
            }
        };
    }
}
