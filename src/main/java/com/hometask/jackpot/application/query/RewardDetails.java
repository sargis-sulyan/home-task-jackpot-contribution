package com.hometask.jackpot.application.query;

import java.math.BigDecimal;
import java.time.Instant;

public record RewardDetails(
        String betId,
        String userId,
        String jackpotId,
        BigDecimal jackpotRewardAmount,
        Instant createdAt
) {
}
