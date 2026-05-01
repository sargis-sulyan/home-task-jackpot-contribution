package com.hometask.jackpot.application.event;

import java.math.BigDecimal;
import java.time.Instant;

public record BetPlacedEvent(
        String betId,
        String userId,
        String jackpotId,
        BigDecimal betAmount,
        Instant occurredAt
) {
}