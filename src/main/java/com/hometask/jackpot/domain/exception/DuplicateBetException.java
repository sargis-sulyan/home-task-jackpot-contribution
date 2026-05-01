package com.hometask.jackpot.domain.exception;

public class DuplicateBetException extends RuntimeException {

    public DuplicateBetException(String betId) {
        super("Bet with id already exists with different data: " + betId);
    }
}