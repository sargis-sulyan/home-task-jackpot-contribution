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
        name = "jackpot_rewards",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_jackpot_rewards_bet_id",
                        columnNames = "bet_id"
                )
        }
)
public class JackpotRewardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bet_id", nullable = false, updatable = false)
    private String betId;

    @Column(name = "user_id", nullable = false, updatable = false)
    private String userId;

    @Column(name = "jackpot_id", nullable = false, updatable = false)
    private String jackpotId;

    @Column(name = "jackpot_reward_amount", nullable = false, precision = 19, scale = 4, updatable = false)
    private BigDecimal jackpotRewardAmount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected JackpotRewardEntity() {
    }

    public JackpotRewardEntity(
            String betId,
            String userId,
            String jackpotId,
            BigDecimal jackpotRewardAmount,
            Instant createdAt
    ) {
        this.betId = betId;
        this.userId = userId;
        this.jackpotId = jackpotId;
        this.jackpotRewardAmount = jackpotRewardAmount;
        this.createdAt = createdAt;
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

    public BigDecimal getJackpotRewardAmount() {
        return jackpotRewardAmount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}