package com.hometask.jackpot.domain.strategy.reward;

import com.hometask.jackpot.domain.model.RewardType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FixedChanceRewardStrategy implements RewardStrategy {

    private final RandomPercentageGenerator randomPercentageGenerator;

    public FixedChanceRewardStrategy(RandomPercentageGenerator randomPercentageGenerator) {
        this.randomPercentageGenerator = randomPercentageGenerator;
    }

    @Override
    public RewardType type() {
        return RewardType.FIXED_CHANCE;
    }

    @Override
    public RewardCalculationResult evaluate(RewardCalculationContext context) {
        BigDecimal chance = context.fixedRewardChancePercentage();
        BigDecimal randomValue = randomPercentageGenerator.nextPercentage();

        boolean won = randomValue.compareTo(chance) < 0;

        return new RewardCalculationResult(
                won,
                won ? context.currentPoolAmount() : BigDecimal.ZERO,
                chance
        );
    }
}