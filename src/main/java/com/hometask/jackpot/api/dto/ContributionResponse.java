package com.hometask.jackpot.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ContributionResponse(
        String betId,
        String userId,
        String jackpotId,
        BigDecimal stakeAmount,
        BigDecimal contributionAmount,
        BigDecimal currentJackpotAmount,
        boolean rewardEvaluated,
        Instant rewardEvaluatedAt,
        Instant createdAt
) {
}