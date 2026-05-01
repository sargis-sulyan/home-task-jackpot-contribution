package com.hometask.jackpot.domain.strategy.reward;

import com.hometask.jackpot.domain.model.RewardType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class VariableChanceRewardStrategy implements RewardStrategy {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    private final RandomPercentageGenerator randomPercentageGenerator;

    public VariableChanceRewardStrategy(RandomPercentageGenerator randomPercentageGenerator) {
        this.randomPercentageGenerator = randomPercentageGenerator;
    }

    @Override
    public RewardType type() {
        return RewardType.VARIABLE_CHANCE;
    }

    @Override
    public RewardCalculationResult evaluate(RewardCalculationContext context) {
        BigDecimal chance = calculateChance(context);
        BigDecimal randomValue = randomPercentageGenerator.nextPercentage();

        boolean won = randomValue.compareTo(chance) < 0;

        return new RewardCalculationResult(
                won,
                won ? context.currentPoolAmount() : BigDecimal.ZERO,
                chance
        );
    }

    private BigDecimal calculateChance(RewardCalculationContext context) {
        if (context.currentPoolAmount().compareTo(context.rewardGuaranteedAtAmount()) >= 0) {
            return ONE_HUNDRED;
        }

        BigDecimal poolGrowth = context.currentPoolAmount()
                .subtract(context.initialPoolAmount())
                .max(BigDecimal.ZERO);

        BigDecimal steps = poolGrowth.divide(
                context.variableRewardStepAmount(),
                0,
                RoundingMode.DOWN
        );

        BigDecimal increase = steps.multiply(context.variableRewardIncreasePerStep());

        return context.variableRewardInitialChancePercentage()
                .add(increase)
                .min(ONE_HUNDRED);
    }
}