package com.hometask.jackpot.application;

import java.math.BigDecimal;

public record ProcessBetCommand(
        String betId,
        String userId,
        String jackpotId,
        BigDecimal betAmount
) {
}