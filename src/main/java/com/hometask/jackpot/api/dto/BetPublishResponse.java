package com.hometask.jackpot.api.dto;

import java.time.Instant;

public record BetPublishResponse(
        String betId,
        String status,
        Instant publishedAt
) {
}