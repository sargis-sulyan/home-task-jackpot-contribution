package com.hometask.jackpot.infrastructure.persistance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "bets")
public class BetEntity {

    @Id
    @Column(name = "bet_id", nullable = false, updatable = false)
    private String betId;

    @Column(name = "user_id", nullable = false, updatable = false)
    private String userId;

    @Column(name = "jackpot_id", nullable = false, updatable = false)
    private String jackpotId;

    @Column(name = "bet_amount", nullable = false, precision = 19, scale = 4, updatable = false)
    private BigDecimal betAmount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected BetEntity() {
    }

    public BetEntity(String betId, String userId, String jackpotId, BigDecimal betAmount, Instant createdAt) {
        this.betId = betId;
        this.userId = userId;
        this.jackpotId = jackpotId;
        this.betAmount = betAmount;
        this.createdAt = createdAt;
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

    public BigDecimal getBetAmount() {
        return betAmount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}