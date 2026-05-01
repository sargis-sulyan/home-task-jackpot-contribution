package com.hometask.jackpot.application.contribution;

import java.math.BigDecimal;
import java.time.Instant;

public record JackpotContributionResult(
        String betId,
        String userId,
        String jackpotId,
        BigDecimal stakeAmount,
        BigDecimal contributionAmount,
        BigDecimal currentJackpotAmount,
        Instant createdAt
) {
}