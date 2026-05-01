package com.hometask.jackpot.infrastructure.persistance.entity;

import com.hometask.jackpot.domain.model.ContributionType;
import com.hometask.jackpot.domain.model.RewardType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;

@Entity
@Table(name = "jackpots")
public class JackpotEntity {

    @Id
    @Column(name = "jackpot_id", nullable = false, updatable = false)
    private String jackpotId;

    @Column(name = "initial_pool_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal initialPoolAmount;

    @Column(name = "current_pool_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal currentPoolAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "contribution_type", nullable = false)
    private ContributionType contributionType;

    @Column(name = "fixed_contribution_percentage", precision = 9, scale = 4)
    private BigDecimal fixedContributionPercentage;

    @Column(name = "variable_contribution_initial_percentage", precision = 9, scale = 4)
    private BigDecimal variableContributionInitialPercentage;

    @Column(name = "variable_contribution_min_percentage", precision = 9, scale = 4)
    private BigDecimal variableContributionMinPercentage;

    @Column(name = "variable_contribution_decrease_per_step", precision = 9, scale = 4)
    private BigDecimal variableContributionDecreasePerStep;

    @Column(name = "variable_contribution_step_amount", precision = 19, scale = 4)
    private BigDecimal variableContributionStepAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "reward_type", nullable = false)
    private RewardType rewardType;

    @Column(name = "fixed_reward_chance_percentage", precision = 9, scale = 4)
    private BigDecimal fixedRewardChancePercentage;

    @Column(name = "variable_reward_initial_chance_percentage", precision = 9, scale = 4)
    private BigDecimal variableRewardInitialChancePercentage;

    @Column(name = "variable_reward_increase_per_step", precision = 9, scale = 4)
    private BigDecimal variableRewardIncreasePerStep;

    @Column(name = "variable_reward_step_amount", precision = 19, scale = 4)
    private BigDecimal variableRewardStepAmount;

    @Column(name = "reward_guaranteed_at_amount", precision = 19, scale = 4)
    private BigDecimal rewardGuaranteedAtAmount;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    protected JackpotEntity() {
    }

    public String getJackpotId() {
        return jackpotId;
    }

    public BigDecimal getInitialPoolAmount() {
        return initialPoolAmount;
    }

    public BigDecimal getCurrentPoolAmount() {
        return currentPoolAmount;
    }

    public ContributionType getContributionType() {
        return contributionType;
    }

    public BigDecimal getFixedContributionPercentage() {
        return fixedContributionPercentage;
    }

    public BigDecimal getVariableContributionInitialPercentage() {
        return variableContributionInitialPercentage;
    }

    public BigDecimal getVariableContributionMinPercentage() {
        return variableContributionMinPercentage;
    }

    public BigDecimal getVariableContributionDecreasePerStep() {
        return variableContributionDecreasePerStep;
    }

    public BigDecimal getVariableContributionStepAmount() {
        return variableContributionStepAmount;
    }

    public RewardType getRewardType() {
        return rewardType;
    }

    public BigDecimal getFixedRewardChancePercentage() {
        return fixedRewardChancePercentage;
    }

    public BigDecimal getVariableRewardInitialChancePercentage() {
        return variableRewardInitialChancePercentage;
    }

    public BigDecimal getVariableRewardIncreasePerStep() {
        return variableRewardIncreasePerStep;
    }

    public BigDecimal getVariableRewardStepAmount() {
        return variableRewardStepAmount;
    }

    public BigDecimal getRewardGuaranteedAtAmount() {
        return rewardGuaranteedAtAmount;
    }

    public Long getVersion() {
        return version;
    }

    public void increaseCurrentPool(BigDecimal contributionAmount) {
        this.currentPoolAmount = this.currentPoolAmount.add(contributionAmount);
    }

    public void resetToInitialPool() {
        this.currentPoolAmount = this.initialPoolAmount;
    }
}