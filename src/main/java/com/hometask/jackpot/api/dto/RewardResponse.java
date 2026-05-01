package com.hometask.jackpot.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record RewardResponse(
        String betId,
        String userId,
        String jackpotId,
        BigDecimal jackpotRewardAmount,
        Instant createdAt
) {
}