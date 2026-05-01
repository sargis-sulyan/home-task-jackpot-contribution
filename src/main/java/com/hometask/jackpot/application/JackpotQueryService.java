package com.hometask.jackpot.application;

import com.hometask.jackpot.api.dto.ContributionResponse;
import com.hometask.jackpot.api.dto.JackpotResponse;
import com.hometask.jackpot.api.dto.RewardResponse;
import com.hometask.jackpot.domain.exception.ContributionNotFoundException;
import com.hometask.jackpot.domain.exception.JackpotNotFoundException;
import com.hometask.jackpot.domain.exception.RewardNotFoundException;
import com.hometask.jackpot.infrastructure.persistance.entity.JackpotContributionEntity;
import com.hometask.jackpot.infrastructure.persistance.entity.JackpotEntity;
import com.hometask.jackpot.infrastructure.persistance.entity.JackpotRewardEntity;
import com.hometask.jackpot.infrastructure.persistance.repository.JackpotContributionRepository;
import com.hometask.jackpot.infrastructure.persistance.repository.JackpotRepository;
import com.hometask.jackpot.infrastructure.persistance.repository.JackpotRewardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JackpotQueryService {

    private final JackpotRepository jackpotRepository;
    private final JackpotContributionRepository contributionRepository;
    private final JackpotRewardRepository rewardRepository;

    public JackpotQueryService(
            JackpotRepository jackpotRepository,
            JackpotContributionRepository contributionRepository,
            JackpotRewardRepository rewardRepository
    ) {
        this.jackpotRepository = jackpotRepository;
        this.contributionRepository = contributionRepository;
        this.rewardRepository = rewardRepository;
    }

    @Transactional(readOnly = true)
    public JackpotResponse getJackpot(String jackpotId) {
        JackpotEntity jackpot = jackpotRepository.findById(jackpotId)
                .orElseThrow(() -> new JackpotNotFoundException(jackpotId));

        return new JackpotResponse(
                jackpot.getJackpotId(),
                jackpot.getInitialPoolAmount(),
                jackpot.getCurrentPoolAmount(),
                jackpot.getContributionType(),
                jackpot.getRewardType()
        );
    }

    @Transactional(readOnly = true)
    public ContributionResponse getContribution(String betId) {
        JackpotContributionEntity contribution = contributionRepository.findByBetId(betId)
                .orElseThrow(() -> new ContributionNotFoundException(betId));

        return new ContributionResponse(
                contribution.getBetId(),
                contribution.getUserId(),
                contribution.getJackpotId(),
                contribution.getStakeAmount(),
                contribution.getContributionAmount(),
                contribution.getCurrentJackpotAmount(),
                contribution.isRewardEvaluated(),
                contribution.getRewardEvaluatedAt(),
                contribution.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public RewardResponse getReward(String betId) {
        JackpotRewardEntity reward = rewardRepository.findByBetId(betId)
                .orElseThrow(() -> new RewardNotFoundException(betId));

        return new RewardResponse(
                reward.getBetId(),
                reward.getUserId(),
                reward.getJackpotId(),
                reward.getJackpotRewardAmount(),
                reward.getCreatedAt()
        );
    }
}