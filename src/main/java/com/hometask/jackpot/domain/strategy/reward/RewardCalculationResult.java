package com.hometask.jackpot.domain.strategy.reward;

import java.math.BigDecimal;

public record RewardCalculationResult(
        boolean won,
        BigDecimal rewardAmount,
        BigDecimal winningChancePercentage
) {
}