package com.hometask.jackpot.application.contribution;

import com.hometask.jackpot.application.ProcessBetCommand;
import com.hometask.jackpot.domain.exception.DuplicateBetException;
import com.hometask.jackpot.domain.exception.JackpotNotFoundException;
import com.hometask.jackpot.domain.strategy.contribution.ContributionCalculationContext;
import com.hometask.jackpot.domain.strategy.contribution.ContributionStrategy;
import com.hometask.jackpot.domain.strategy.contribution.ContributionStrategyResolver;
import com.hometask.jackpot.infrastructure.persistence.entity.BetEntity;
import com.hometask.jackpot.infrastructure.persistence.entity.JackpotContributionEntity;
import com.hometask.jackpot.infrastructure.persistence.entity.JackpotEntity;
import com.hometask.jackpot.infrastructure.persistence.repository.BetRepository;
import com.hometask.jackpot.infrastructure.persistence.repository.JackpotContributionRepository;
import com.hometask.jackpot.infrastructure.persistence.repository.JackpotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;

@Service
public class JackpotContributionService {

    private final BetRepository betRepository;
    private final JackpotRepository jackpotRepository;
    private final JackpotContributionRepository jackpotContributionRepository;
    private final ContributionStrategyResolver contributionStrategyResolver;
    private final Clock clock;

    public JackpotContributionService(
            BetRepository betRepository,
            JackpotRepository jackpotRepository,
            JackpotContributionRepository jackpotContributionRepository,
            ContributionStrategyResolver contributionStrategyResolver,
            Clock clock
    ) {
        this.betRepository = betRepository;
        this.jackpotRepository = jackpotRepository;
        this.jackpotContributionRepository = jackpotContributionRepository;
        this.contributionStrategyResolver = contributionStrategyResolver;
        this.clock = clock;
    }

    @Transactional
    public JackpotContributionResult contribute(ProcessBetCommand command) {
        return jackpotContributionRepository.findByBetId(command.betId())
                .map(this::toResult)
                .orElseGet(() -> processNewContribution(command));
    }

    private JackpotContributionResult processNewContribution(ProcessBetCommand command) {
        saveBetIfNeeded(command);

        JackpotEntity jackpot = jackpotRepository.findById(command.jackpotId())
                .orElseThrow(() -> new JackpotNotFoundException(command.jackpotId()));

        ContributionStrategy strategy = contributionStrategyResolver.resolve(jackpot.getContributionType());

        BigDecimal contributionAmount = strategy.calculate(toContributionContext(command, jackpot));

        jackpot.increaseCurrentPool(contributionAmount);

        JackpotContributionEntity contribution = new JackpotContributionEntity(
                command.betId(),
                command.userId(),
                command.jackpotId(),
                command.betAmount(),
                contributionAmount,
                jackpot.getCurrentPoolAmount(),
                Instant.now(clock)
        );

        JackpotContributionEntity savedContribution = jackpotContributionRepository.save(contribution);

        return toResult(savedContribution);
    }

    private void saveBetIfNeeded(ProcessBetCommand command) {
        betRepository.findById(command.betId())
                .ifPresentOrElse(
                        existingBet -> validateSameBet(command, existingBet),
                        () -> betRepository.save(new BetEntity(
                                command.betId(),
                                command.userId(),
                                command.jackpotId(),
                                command.betAmount(),
                                Instant.now(clock)
                        ))
                );
    }

    private void validateSameBet(ProcessBetCommand command, BetEntity existingBet) {
        boolean sameBet =
                existingBet.getUserId().equals(command.userId())
                        && existingBet.getJackpotId().equals(command.jackpotId())
                        && existingBet.getBetAmount().compareTo(command.betAmount()) == 0;

        if (!sameBet) {
            throw new DuplicateBetException(command.betId());
        }
    }

    private ContributionCalculationContext toContributionContext(
            ProcessBetCommand command,
            JackpotEntity jackpot
    ) {
        return new ContributionCalculationContext(
                command.betAmount(),
                jackpot.getInitialPoolAmount(),
                jackpot.getCurrentPoolAmount(),
                jackpot.getFixedContributionPercentage(),
                jackpot.getVariableContributionInitialPercentage(),
                jackpot.getVariableContributionMinPercentage(),
                jackpot.getVariableContributionDecreasePerStep(),
                jackpot.getVariableContributionStepAmount()
        );
    }

    private JackpotContributionResult toResult(JackpotContributionEntity contribution) {
        return new JackpotContributionResult(
                contribution.getBetId(),
                contribution.getUserId(),
                contribution.getJackpotId(),
                contribution.getStakeAmount(),
                contribution.getContributionAmount(),
                contribution.getCurrentJackpotAmount(),
                contribution.getCreatedAt()
        );
    }
}