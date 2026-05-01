package com.hometask.jackpot.application.query;

import java.math.BigDecimal;
import java.time.Instant;

public record ContributionDetails(
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
