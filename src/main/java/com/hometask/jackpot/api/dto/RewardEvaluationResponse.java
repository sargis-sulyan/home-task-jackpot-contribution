package com.hometask.jackpot.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record RewardEvaluationResponse(
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