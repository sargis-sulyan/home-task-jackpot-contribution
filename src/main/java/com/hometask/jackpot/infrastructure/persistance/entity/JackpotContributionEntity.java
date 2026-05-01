package com.hometask.jackpot.infrastructure.persistance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name = "jackpot_contributions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_jackpot_contributions_bet_id",
                        columnNames = "bet_id"
                )
        }
)
public class JackpotContributionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bet_id", nullable = false, updatable = false)
    private String betId;

    @Column(name = "user_id", nullable = false, updatable = false)
    private String userId;

    @Column(name = "jackpot_id", nullable = false, updatable = false)
    private String jackpotId;

    @Column(name = "stake_amount", nullable = false, precision = 19, scale = 4, updatable = false)
    private BigDecimal stakeAmount;

    @Column(name = "contribution_amount", nullable = false, precision = 19, scale = 4, updatable = false)
    private BigDecimal contributionAmount;

    @Column(name = "current_jackpot_amount", nullable = false, precision = 19, scale = 4, updatable = false)
    private BigDecimal currentJackpotAmount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "reward_evaluated", nullable = false)
    private boolean rewardEvaluated;

    @Column(name = "reward_evaluated_at")
    private Instant rewardEvaluatedAt;

    protected JackpotContributionEntity() {
    }

    public JackpotContributionEntity(
            String betId,
            String userId,
            String jackpotId,
            BigDecimal stakeAmount,
            BigDecimal contributionAmount,
            BigDecimal currentJackpotAmount,
            Instant createdAt
    ) {
        this.betId = betId;
        this.userId = userId;
        this.jackpotId = jackpotId;
        this.stakeAmount = stakeAmount;
        this.contributionAmount = contributionAmount;
        this.currentJackpotAmount = currentJackpotAmount;
        this.createdAt = createdAt;
        this.rewardEvaluated = false;
    }

    public Long getId() {
        return id;
    }

    public String getBetId() {
        return betId;
    }

    public String getUserId() {
        return userId;
    }

    public String getJackpotId() {
        return jackpotId;
    }

    public BigDecimal getStakeAmount() {
        return stakeAmount;
    }

    public BigDecimal getContributionAmount() {
        return contributionAmount;
    }

    public BigDecimal getCurrentJackpotAmount() {
        return currentJackpotAmount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isRewardEvaluated() {
        return rewardEvaluated;
    }

    public Instant getRewardEvaluatedAt() {
        return rewardEvaluatedAt;
    }

    public void markRewardEvaluated(Instant evaluatedAt) {
        this.rewardEvaluated = true;
        this.rewardEvaluatedAt = evaluatedAt;
    }
}