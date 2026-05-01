package com.hometask.jackpot.domain.exception;

public class BetPublishingException extends RuntimeException {

    public BetPublishingException(String betId, Throwable cause) {
        super("Failed to publish bet to Kafka: " + betId, cause);
    }
}