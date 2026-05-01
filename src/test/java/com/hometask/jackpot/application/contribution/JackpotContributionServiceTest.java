package com.hometask.jackpot.application.contribution;

import com.hometask.jackpot.application.ProcessBetCommand;
import com.hometask.jackpot.domain.exception.DuplicateBetException;
import com.hometask.jackpot.domain.exception.JackpotNotFoundException;
import com.hometask.jackpot.domain.model.ContributionType;
import com.hometask.jackpot.domain.model.RewardType;
import com.hometask.jackpot.domain.strategy.contribution.ContributionCalculationContext;
import com.hometask.jackpot.domain.strategy.contribution.ContributionStrategy;
import com.hometask.jackpot.domain.strategy.contribution.ContributionStrategyResolver;
import com.hometask.jackpot.infrastructure.persistence.entity.BetEntity;
import com.hometask.jackpot.infrastructure.persistence.entity.JackpotContributionEntity;
import com.hometask.jackpot.infrastructure.persistence.entity.JackpotEntity;
import com.hometask.jackpot.infrastructure.persistence.repository.BetRepository;
import com.hometask.jackpot.infrastructure.persistence.repository.JackpotContributionRepository;
import com.hometask.jackpot.infrastructure.persistence.repository.JackpotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.BeanUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JackpotContributionServiceTest {

    private static final Instant NOW = Instant.parse("2026-05-01T10:15:30Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

    @Mock
    private BetRepository betRepository;

    @Mock
    private JackpotRepository jackpotRepository;

    @Mock
    private JackpotContributionRepository jackpotContributionRepository;

    @Mock
    private ContributionStrategyResolver contributionStrategyResolver;

    @Mock
    private ContributionStrategy contributionStrategy;

    private JackpotContributionService service;

    @BeforeEach
    void setUp() {
        service = new JackpotContributionService(
                betRepository,
                jackpotRepository,
                jackpotContributionRepository,
                contributionStrategyResolver,
                CLOCK
        );
    }

    @Test
    void shouldProcessNewBetContribution() {
        // Given
        ProcessBetCommand command = command();
        JackpotEntity jackpot = jackpot();

        when(jackpotContributionRepository.findByBetId("bet-1")).thenReturn(Optional.empty());
        when(betRepository.findById("bet-1")).thenReturn(Optional.empty());
        when(betRepository.save(any(BetEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jackpotRepository.findById("jackpot-1")).thenReturn(Optional.of(jackpot));
        when(contributionStrategyResolver.resolve(ContributionType.FIXED_PERCENTAGE)).thenReturn(contributionStrategy);
        when(contributionStrategy.calculate(any(ContributionCalculationContext.class)))
                .thenReturn(new BigDecimal("5.0000"));
        when(jackpotContributionRepository.save(any(JackpotContributionEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        JackpotContributionResult result = service.contribute(command);

        // Then
        assertThat(result.betId()).isEqualTo("bet-1");
        assertThat(result.userId()).isEqualTo("user-1");
        assertThat(result.jackpotId()).isEqualTo("jackpot-1");
        assertThat(result.stakeAmount()).isEqualByComparingTo("100.0000");
        assertThat(result.contributionAmount()).isEqualByComparingTo("5.0000");
        assertThat(result.currentJackpotAmount()).isEqualByComparingTo("1005.0000");
        assertThat(result.createdAt()).isEqualTo(NOW);
        assertThat(jackpot.getCurrentPoolAmount()).isEqualByComparingTo("1005.0000");

        ArgumentCaptor<BetEntity> betCaptor = ArgumentCaptor.forClass(BetEntity.class);
        verify(betRepository).save(betCaptor.capture());
        assertThat(betCaptor.getValue().getBetId()).isEqualTo("bet-1");
        assertThat(betCaptor.getValue().getCreatedAt()).isEqualTo(NOW);

        ArgumentCaptor<JackpotContributionEntity> contributionCaptor =
                ArgumentCaptor.forClass(JackpotContributionEntity.class);
        verify(jackpotContributionRepository).save(contributionCaptor.capture());
        assertThat(contributionCaptor.getValue().getContributionAmount()).isEqualByComparingTo("5.0000");
        assertThat(contributionCaptor.getValue().getCurrentJackpotAmount()).isEqualByComparingTo("1005.0000");

        verify(jackpotRepository).findById("jackpot-1");
    }

    @Test
    void shouldNotApplyContributionTwiceForSameBet() {
        // Given
        JackpotContributionEntity existingContribution = contribution(
                "bet-1",
                new BigDecimal("5.0000"),
                new BigDecimal("1005.0000"),
                NOW
        );
        when(jackpotContributionRepository.findByBetId("bet-1"))
                .thenReturn(Optional.of(existingContribution));

        // When
        JackpotContributionResult result = service.contribute(command());

        // Then
        assertThat(result.betId()).isEqualTo("bet-1");
        assertThat(result.contributionAmount()).isEqualByComparingTo("5.0000");
        assertThat(result.currentJackpotAmount()).isEqualByComparingTo("1005.0000");
        assertThat(result.createdAt()).isEqualTo(NOW);

        verifyNoInteractions(betRepository, jackpotRepository, contributionStrategyResolver, contributionStrategy);
        verify(jackpotContributionRepository, never()).save(any(JackpotContributionEntity.class));
    }

    @Test
    void shouldThrowDuplicateBetExceptionWhenExistingBetHasDifferentData() {
        // Given
        BetEntity existingBet = new BetEntity(
                "bet-1",
                "another-user",
                "jackpot-1",
                new BigDecimal("100.0000"),
                NOW
        );

        when(jackpotContributionRepository.findByBetId("bet-1")).thenReturn(Optional.empty());
        when(betRepository.findById("bet-1")).thenReturn(Optional.of(existingBet));

        // When / Then
        assertThatThrownBy(() -> service.contribute(command()))
                .isInstanceOf(DuplicateBetException.class);

        verify(betRepository, never()).save(any(BetEntity.class));
        verifyNoInteractions(jackpotRepository, contributionStrategyResolver, contributionStrategy);
        verify(jackpotContributionRepository, never()).save(any(JackpotContributionEntity.class));
    }

    @Test
    void shouldThrowJackpotNotFoundExceptionWhenJackpotDoesNotExist() {
        // Given
        when(jackpotContributionRepository.findByBetId("bet-1")).thenReturn(Optional.empty());
        when(betRepository.findById("bet-1")).thenReturn(Optional.empty());
        when(betRepository.save(any(BetEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jackpotRepository.findById("jackpot-1")).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> service.contribute(command()))
                .isInstanceOf(JackpotNotFoundException.class);

        verify(betRepository).save(any(BetEntity.class));
        verify(jackpotContributionRepository, never()).save(any(JackpotContributionEntity.class));
        verifyNoInteractions(contributionStrategyResolver, contributionStrategy);
    }

    private ProcessBetCommand command() {
        return new ProcessBetCommand(
                "bet-1",
                "user-1",
                "jackpot-1",
                new BigDecimal("100.0000")
        );
    }

    private JackpotContributionEntity contribution(
            String betId,
            BigDecimal contributionAmount,
            BigDecimal currentJackpotAmount,
            Instant createdAt
    ) {
        return new JackpotContributionEntity(
                betId,
                "user-1",
                "jackpot-1",
                new BigDecimal("100.0000"),
                contributionAmount,
                currentJackpotAmount,
                createdAt
        );
    }

    private JackpotEntity jackpot() {
        JackpotEntity jackpot = BeanUtils.instantiateClass(JackpotEntity.class);
        ReflectionTestUtils.setField(jackpot, "jackpotId", "jackpot-1");
        ReflectionTestUtils.setField(jackpot, "initialPoolAmount", new BigDecimal("1000.0000"));
        ReflectionTestUtils.setField(jackpot, "currentPoolAmount", new BigDecimal("1000.0000"));
        ReflectionTestUtils.setField(jackpot, "contributionType", ContributionType.FIXED_PERCENTAGE);
        ReflectionTestUtils.setField(jackpot, "fixedContributionPercentage", new BigDecimal("5.0000"));
        ReflectionTestUtils.setField(jackpot, "variableContributionInitialPercentage", new BigDecimal("10.0000"));
        ReflectionTestUtils.setField(jackpot, "variableContributionMinPercentage", new BigDecimal("3.0000"));
        ReflectionTestUtils.setField(jackpot, "variableContributionDecreasePerStep", new BigDecimal("1.0000"));
        ReflectionTestUtils.setField(jackpot, "variableContributionStepAmount", new BigDecimal("500.0000"));
        ReflectionTestUtils.setField(jackpot, "rewardType", RewardType.FIXED_CHANCE);
        ReflectionTestUtils.setField(jackpot, "fixedRewardChancePercentage", new BigDecimal("10.0000"));
        ReflectionTestUtils.setField(jackpot, "variableRewardInitialChancePercentage", new BigDecimal("5.0000"));
        ReflectionTestUtils.setField(jackpot, "variableRewardIncreasePerStep", new BigDecimal("2.0000"));
        ReflectionTestUtils.setField(jackpot, "variableRewardStepAmount", new BigDecimal("500.0000"));
        ReflectionTestUtils.setField(jackpot, "rewardGuaranteedAtAmount", new BigDecimal("5000.0000"));
        ReflectionTestUtils.setField(jackpot, "version", 0L);
        return jackpot;
    }
}
