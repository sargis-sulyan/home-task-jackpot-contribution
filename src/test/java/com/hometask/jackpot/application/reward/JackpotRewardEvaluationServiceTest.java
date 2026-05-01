package com.hometask.jackpot.application.reward;

import com.hometask.jackpot.domain.exception.ContributionNotFoundException;
import com.hometask.jackpot.domain.model.ContributionType;
import com.hometask.jackpot.domain.model.RewardType;
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
class JackpotRewardEvaluationServiceTest {

    private static final Instant CREATED_AT = Instant.parse("2026-05-01T09:00:00Z");
    private static final Instant EVALUATED_AT = Instant.parse("2026-05-01T10:15:30Z");
    private static final Clock CLOCK = Clock.fixed(EVALUATED_AT, ZoneOffset.UTC);

    @Mock
    private JackpotContributionRepository jackpotContributionRepository;

    @Mock
    private JackpotRewardRepository jackpotRewardRepository;

    @Mock
    private JackpotRepository jackpotRepository;

    @Mock
    private RewardStrategyResolver rewardStrategyResolver;

    @Mock
    private RewardStrategy rewardStrategy;

    private JackpotRewardEvaluationService service;

    @BeforeEach
    void setUp() {
        service = new JackpotRewardEvaluationService(
                jackpotContributionRepository,
                jackpotRewardRepository,
                jackpotRepository,
                rewardStrategyResolver,
                CLOCK
        );
    }

    @Test
    void shouldThrowContributionNotFoundExceptionWhenContributionDoesNotExist() {
        // Given
        when(jackpotContributionRepository.findByBetId("bet-1")).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> service.evaluate("bet-1"))
                .isInstanceOf(ContributionNotFoundException.class);

        verifyNoInteractions(jackpotRewardRepository, jackpotRepository, rewardStrategyResolver, rewardStrategy);
    }

    @Test
    void shouldReturnExistingWonResultWithoutReevaluatingReward() {
        // Given
        JackpotContributionEntity contribution = evaluatedContribution();
        JackpotRewardEntity reward = new JackpotRewardEntity(
                "bet-1",
                "user-1",
                "jackpot-1",
                new BigDecimal("1250.0000"),
                EVALUATED_AT
        );

        when(jackpotContributionRepository.findByBetId("bet-1")).thenReturn(Optional.of(contribution));
        when(jackpotRewardRepository.findByBetId("bet-1")).thenReturn(Optional.of(reward));

        // When
        JackpotRewardEvaluationResult result = service.evaluate("bet-1");

        // Then
        assertThat(result.won()).isTrue();
        assertThat(result.rewardAmount()).isEqualByComparingTo("1250.0000");
        assertThat(result.currentJackpotAmount()).isEqualByComparingTo("1250.0000");
        assertThat(result.winningChancePercentage()).isNull();
        assertThat(result.evaluatedAt()).isEqualTo(EVALUATED_AT);

        verifyNoInteractions(jackpotRepository, rewardStrategyResolver, rewardStrategy);
        verify(jackpotRewardRepository, never()).save(any(JackpotRewardEntity.class));
    }

    @Test
    void shouldReturnExistingLostResultWithoutReevaluatingReward() {
        // Given
        JackpotContributionEntity contribution = evaluatedContribution();

        when(jackpotContributionRepository.findByBetId("bet-1")).thenReturn(Optional.of(contribution));
        when(jackpotRewardRepository.findByBetId("bet-1")).thenReturn(Optional.empty());

        // When
        JackpotRewardEvaluationResult result = service.evaluate("bet-1");

        // Then
        assertThat(result.won()).isFalse();
        assertThat(result.rewardAmount()).isEqualByComparingTo("0");
        assertThat(result.currentJackpotAmount()).isEqualByComparingTo("1250.0000");
        assertThat(result.winningChancePercentage()).isNull();
        assertThat(result.evaluatedAt()).isEqualTo(EVALUATED_AT);

        verifyNoInteractions(jackpotRepository, rewardStrategyResolver, rewardStrategy);
        verify(jackpotRewardRepository, never()).save(any(JackpotRewardEntity.class));
    }

    @Test
    void shouldMarkContributionEvaluatedAndNotResetJackpotWhenRewardIsLost() {
        // Given
        JackpotContributionEntity contribution = contribution();
        JackpotEntity jackpot = jackpot(new BigDecimal("1250.0000"));

        when(jackpotContributionRepository.findByBetId("bet-1")).thenReturn(Optional.of(contribution));
        when(jackpotRepository.findById("jackpot-1")).thenReturn(Optional.of(jackpot));
        when(rewardStrategyResolver.resolve(RewardType.FIXED_CHANCE)).thenReturn(rewardStrategy);
        when(rewardStrategy.evaluate(any(RewardCalculationContext.class)))
                .thenReturn(new RewardCalculationResult(false, BigDecimal.ZERO, new BigDecimal("10.0000")));

        // When
        JackpotRewardEvaluationResult result = service.evaluate("bet-1");

        // Then
        assertThat(contribution.isRewardEvaluated()).isTrue();
        assertThat(contribution.getRewardEvaluatedAt()).isEqualTo(EVALUATED_AT);
        assertThat(jackpot.getCurrentPoolAmount()).isEqualByComparingTo("1250.0000");
        assertThat(result.won()).isFalse();
        assertThat(result.rewardAmount()).isEqualByComparingTo("0");
        assertThat(result.currentJackpotAmount()).isEqualByComparingTo("1250.0000");
        assertThat(result.winningChancePercentage()).isEqualByComparingTo("10.0000");
        assertThat(result.evaluatedAt()).isEqualTo(EVALUATED_AT);

        verify(jackpotRewardRepository, never()).save(any(JackpotRewardEntity.class));
    }

    @Test
    void shouldResetJackpotWhenRewardIsWon() {
        // Given
        JackpotContributionEntity contribution = contribution();
        JackpotEntity jackpot = jackpot(new BigDecimal("1250.0000"));

        when(jackpotContributionRepository.findByBetId("bet-1")).thenReturn(Optional.of(contribution));
        when(jackpotRepository.findById("jackpot-1")).thenReturn(Optional.of(jackpot));
        when(rewardStrategyResolver.resolve(RewardType.FIXED_CHANCE)).thenReturn(rewardStrategy);
        when(rewardStrategy.evaluate(any(RewardCalculationContext.class)))
                .thenReturn(new RewardCalculationResult(
                        true,
                        new BigDecimal("1250.0000"),
                        new BigDecimal("10.0000")
                ));
        when(jackpotRewardRepository.save(any(JackpotRewardEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        JackpotRewardEvaluationResult result = service.evaluate("bet-1");

        // Then
        assertThat(contribution.isRewardEvaluated()).isTrue();
        assertThat(contribution.getRewardEvaluatedAt()).isEqualTo(EVALUATED_AT);
        assertThat(jackpot.getCurrentPoolAmount()).isEqualByComparingTo("1000.0000");
        assertThat(result.won()).isTrue();
        assertThat(result.rewardAmount()).isEqualByComparingTo("1250.0000");
        assertThat(result.currentJackpotAmount()).isEqualByComparingTo("1000.0000");
        assertThat(result.winningChancePercentage()).isEqualByComparingTo("10.0000");
        assertThat(result.evaluatedAt()).isEqualTo(EVALUATED_AT);

        ArgumentCaptor<JackpotRewardEntity> rewardCaptor = ArgumentCaptor.forClass(JackpotRewardEntity.class);
        verify(jackpotRewardRepository).save(rewardCaptor.capture());
        assertThat(rewardCaptor.getValue().getBetId()).isEqualTo("bet-1");
        assertThat(rewardCaptor.getValue().getUserId()).isEqualTo("user-1");
        assertThat(rewardCaptor.getValue().getJackpotId()).isEqualTo("jackpot-1");
        assertThat(rewardCaptor.getValue().getJackpotRewardAmount()).isEqualByComparingTo("1250.0000");
        assertThat(rewardCaptor.getValue().getCreatedAt()).isEqualTo(EVALUATED_AT);
    }

    private JackpotContributionEntity contribution() {
        return new JackpotContributionEntity(
                "bet-1",
                "user-1",
                "jackpot-1",
                new BigDecimal("100.0000"),
                new BigDecimal("5.0000"),
                new BigDecimal("1250.0000"),
                CREATED_AT
        );
    }

    private JackpotContributionEntity evaluatedContribution() {
        JackpotContributionEntity contribution = contribution();
        contribution.markRewardEvaluated(EVALUATED_AT);
        return contribution;
    }

    private JackpotEntity jackpot(BigDecimal currentPoolAmount) {
        JackpotEntity jackpot = BeanUtils.instantiateClass(JackpotEntity.class);
        ReflectionTestUtils.setField(jackpot, "jackpotId", "jackpot-1");
        ReflectionTestUtils.setField(jackpot, "initialPoolAmount", new BigDecimal("1000.0000"));
        ReflectionTestUtils.setField(jackpot, "currentPoolAmount", currentPoolAmount);
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
