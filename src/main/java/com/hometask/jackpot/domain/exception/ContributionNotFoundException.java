package com.hometask.jackpot.domain.exception;

public class ContributionNotFoundException extends RuntimeException {

    public ContributionNotFoundException(String betId) {
        super("Contribution not found for betId: " + betId);
    }
}