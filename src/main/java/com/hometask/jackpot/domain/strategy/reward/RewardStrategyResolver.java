package com.hometask.jackpot.domain.strategy.reward;

import com.hometask.jackpot.domain.exception.UnsupportedJackpotConfigurationException;
import com.hometask.jackpot.domain.model.RewardType;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class RewardStrategyResolver {

    private final Map<RewardType, RewardStrategy> strategies;

    public RewardStrategyResolver(List<RewardStrategy> strategies) {
        EnumMap<RewardType, RewardStrategy> strategyMap = new EnumMap<>(RewardType.class);

        for (RewardStrategy strategy : strategies) {
            RewardStrategy existing = strategyMap.put(strategy.type(), strategy);

            if (existing != null) {
                throw new IllegalStateException(
                        "Duplicate reward strategy for type: " + strategy.type()
                );
            }
        }

        this.strategies = Collections.unmodifiableMap(strategyMap);
    }

    public RewardStrategy resolve(RewardType type) {
        RewardStrategy strategy = strategies.get(type);

        if (strategy == null) {
            throw new UnsupportedJackpotConfigurationException(
                    "Unsupported reward type: " + type
            );
        }

        return strategy;
    }
}