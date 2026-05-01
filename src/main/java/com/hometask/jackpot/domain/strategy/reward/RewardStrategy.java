package com.hometask.jackpot.domain.strategy.reward;

import com.hometask.jackpot.domain.model.RewardType;

public interface RewardStrategy {

    RewardType type();

    RewardCalculationResult evaluate(RewardCalculationContext context);
}