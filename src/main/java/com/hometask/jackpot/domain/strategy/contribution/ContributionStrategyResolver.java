package com.hometask.jackpot.domain.strategy.contribution;

import com.hometask.jackpot.domain.exception.UnsupportedJackpotConfigurationException;
import com.hometask.jackpot.domain.model.ContributionType;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class ContributionStrategyResolver {

    private final Map<ContributionType, ContributionStrategy> strategies;

    public ContributionStrategyResolver(List<ContributionStrategy> strategies) {
        EnumMap<ContributionType, ContributionStrategy> strategyMap = new EnumMap<>(ContributionType.class);

        for (ContributionStrategy strategy : strategies) {
            ContributionStrategy existing = strategyMap.put(strategy.type(), strategy);

            if (existing != null) {
                throw new IllegalStateException(
                        "Duplicate contribution strategy for type: " + strategy.type()
                );
            }
        }

        this.strategies = Collections.unmodifiableMap(strategyMap);
    }

    public ContributionStrategy resolve(ContributionType type) {
        ContributionStrategy strategy = strategies.get(type);

        if (strategy == null) {
            throw new UnsupportedJackpotConfigurationException(
                    "Unsupported contribution type: " + type
            );
        }

        return strategy;
    }
}