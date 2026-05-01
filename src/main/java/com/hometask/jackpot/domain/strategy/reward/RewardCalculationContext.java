package com.hometask.jackpot.domain.strategy.reward;

import java.math.BigDecimal;

public record RewardCalculationContext(
        BigDecimal initialPoolAmount,
        BigDecimal currentPoolAmount,
        BigDecimal fixedRewardChancePercentage,
        BigDecimal variableRewardInitialChancePercentage,
        BigDecimal variableRewardIncreasePerStep,
        BigDecimal variableRewardStepAmount,
        BigDecimal rewardGuaranteedAtAmount
) {
}