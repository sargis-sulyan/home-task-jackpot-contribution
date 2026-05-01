package com.hometask.jackpot.application.reward;

import java.math.BigDecimal;
import java.time.Instant;

public record JackpotRewardEvaluationResult(
        String betId,
        String userId,
        String jackpotId,
        boolean won,
        BigDecimal rewardAmount,
        BigDecimal currentJackpotAmount,
        BigDecimal winningChancePercentage,
        Instant evaluatedAt
) {
}