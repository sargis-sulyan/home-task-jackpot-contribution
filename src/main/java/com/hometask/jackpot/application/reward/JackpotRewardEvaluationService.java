package com.hometask.jackpot.application.reward;

import com.hometask.jackpot.domain.exception.ContributionNotFoundException;
import com.hometask.jackpot.domain.exception.JackpotNotFoundException;
import com.hometask.jackpot.domain.strategy.reward.RewardCalculationContext;
import com.hometask.jackpot.domain.strategy.reward.RewardCalculationResult;
import com.hometask.jackpot.domain.strategy.reward.RewardStrategy;
import com.hometask.jackpot.domain.strategy.reward.RewardStrategyResolver;
import com.hometask.jackpot.infrastructure.persistance.entity.JackpotContributionEntity;
import com.hometask.jackpot.infrastructure.persistance.entity.JackpotEntity;
import com.hometask.jackpot.infrastructure.persistance.entity.JackpotRewardEntity;
import com.hometask.jackpot.infrastructure.persistance.repository.JackpotContributionRepository;
import com.hometask.jackpot.infrastructure.persistance.repository.JackpotRepository;
import com.hometask.jackpot.infrastructure.persistance.repository.JackpotRewardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;

@Service
public class JackpotRewardEvaluationService {

    private final JackpotContributionRepository jackpotContributionRepository;
    private final JackpotRewardRepository jackpotRewardRepository;
    private final JackpotRepository jackpotRepository;
    private final RewardStrategyResolver rewardStrategyResolver;
    private final Clock clock;

    public JackpotRewardEvaluationService(
            JackpotContributionRepository jackpotContributionRepository,
            JackpotRewardRepository jackpotRewardRepository,
            JackpotRepository jackpotRepository,
            RewardStrategyResolver rewardStrategyResolver,
            Clock clock
    ) {
        this.jackpotContributionRepository = jackpotContributionRepository;
        this.jackpotRewardRepository = jackpotRewardRepository;
        this.jackpotRepository = jackpotRepository;
        this.rewardStrategyResolver = rewardStrategyResolver;
        this.clock = clock;
    }

    @Transactional
    public JackpotRewardEvaluationResult evaluate(String betId) {
        JackpotContributionEntity contribution = jackpotContributionRepository.findByBetId(betId)
                .orElseThrow(() -> new ContributionNotFoundException(betId));

        if (contribution.isRewardEvaluated()) {
            return buildAlreadyEvaluatedResult(contribution);
        }

        JackpotEntity jackpot = jackpotRepository.findById(contribution.getJackpotId())
                .orElseThrow(() -> new JackpotNotFoundException(contribution.getJackpotId()));

        RewardStrategy rewardStrategy = rewardStrategyResolver.resolve(jackpot.getRewardType());

        RewardCalculationResult rewardCalculationResult = rewardStrategy.evaluate(
                toRewardCalculationContext(jackpot)
        );

        Instant evaluatedAt = Instant.now(clock);

        contribution.markRewardEvaluated(evaluatedAt);

        if (rewardCalculationResult.won()) {
            JackpotRewardEntity reward = new JackpotRewardEntity(
                    contribution.getBetId(),
                    contribution.getUserId(),
                    contribution.getJackpotId(),
                    rewardCalculationResult.rewardAmount(),
                    evaluatedAt
            );

            jackpotRewardRepository.save(reward);
            jackpot.resetToInitialPool();
        }

        return new JackpotRewardEvaluationResult(
                contribution.getBetId(),
                contribution.getUserId(),
                contribution.getJackpotId(),
                rewardCalculationResult.won(),
                rewardCalculationResult.rewardAmount(),
                jackpot.getCurrentPoolAmount(),
                rewardCalculationResult.winningChancePercentage(),
                evaluatedAt
        );
    }

    private JackpotRewardEvaluationResult buildAlreadyEvaluatedResult(
            JackpotContributionEntity contribution
    ) {
        return jackpotRewardRepository.findByBetId(contribution.getBetId())
                .map(reward -> new JackpotRewardEvaluationResult(
                        contribution.getBetId(),
                        contribution.getUserId(),
                        contribution.getJackpotId(),
                        true,
                        reward.getJackpotRewardAmount(),
                        contribution.getCurrentJackpotAmount(),
                        null,
                        contribution.getRewardEvaluatedAt()
                ))
                .orElseGet(() -> new JackpotRewardEvaluationResult(
                        contribution.getBetId(),
                        contribution.getUserId(),
                        contribution.getJackpotId(),
                        false,
                        BigDecimal.ZERO,
                        contribution.getCurrentJackpotAmount(),
                        null,
                        contribution.getRewardEvaluatedAt()
                ));
    }

    private RewardCalculationContext toRewardCalculationContext(JackpotEntity jackpot) {
        return new RewardCalculationContext(
                jackpot.getInitialPoolAmount(),
                jackpot.getCurrentPoolAmount(),
                jackpot.getFixedRewardChancePercentage(),
                jackpot.getVariableRewardInitialChancePercentage(),
                jackpot.getVariableRewardIncreasePerStep(),
                jackpot.getVariableRewardStepAmount(),
                jackpot.getRewardGuaranteedAtAmount()
        );
    }
}