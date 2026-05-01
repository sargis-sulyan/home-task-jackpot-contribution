package com.hometask.jackpot.application.event;

public interface BetEventPublisher {

    void publish(BetPlacedEvent event);
}
