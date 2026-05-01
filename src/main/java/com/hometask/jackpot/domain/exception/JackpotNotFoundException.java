package com.hometask.jackpot.domain.exception;

public class JackpotNotFoundException extends RuntimeException {

    public JackpotNotFoundException(String jackpotId) {
        super("Jackpot not found: " + jackpotId);
    }
}