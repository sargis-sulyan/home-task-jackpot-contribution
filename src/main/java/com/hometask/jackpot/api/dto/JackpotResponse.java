package com.hometask.jackpot.api.dto;

import com.hometask.jackpot.domain.model.ContributionType;
import com.hometask.jackpot.domain.model.RewardType;

import java.math.BigDecimal;

public record JackpotResponse(
        String jackpotId,
        BigDecimal initialPoolAmount,
        BigDecimal currentPoolAmount,
        ContributionType contributionType,
        RewardType rewardType
) {
}