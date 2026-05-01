package com.hometask.jackpot.domain.exception;

public class RewardNotFoundException extends RuntimeException {

    public RewardNotFoundException(String betId) {
        super("Reward not found for betId: " + betId);
    }
}