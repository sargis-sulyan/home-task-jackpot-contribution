package com.hometask.jackpot.application.event;

import com.hometask.jackpot.application.event.BetPlacedEvent;

public interface BetEventPublisher {

    void publish(BetPlacedEvent event);
}