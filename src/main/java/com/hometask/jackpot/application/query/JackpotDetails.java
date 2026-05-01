package com.hometask.jackpot.application.query;

import com.hometask.jackpot.domain.model.ContributionType;
import com.hometask.jackpot.domain.model.RewardType;

import java.math.BigDecimal;

public record JackpotDetails(
        String jackpotId,
        BigDecimal initialPoolAmount,
        BigDecimal currentPoolAmount,
        ContributionType contributionType,
        RewardType rewardType
) {
}
