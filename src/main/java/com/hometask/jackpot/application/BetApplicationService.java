package com.hometask.jackpot.application;

import com.hometask.jackpot.application.event.BetEventPublisher;
import com.hometask.jackpot.application.event.BetPlacedEvent;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;

@Service
public class BetApplicationService {

    private final BetEventPublisher betEventPublisher;
    private final Clock clock;

    public BetApplicationService(BetEventPublisher betEventPublisher, Clock clock) {
        this.betEventPublisher = betEventPublisher;
        this.clock = clock;
    }

    public Instant publishBet(
            String betId,
            String userId,
            String jackpotId,
            BigDecimal betAmount
    ) {
        Instant publishedAt = Instant.now(clock);

        BetPlacedEvent event = new BetPlacedEvent(
                betId,
                userId,
                jackpotId,
                betAmount,
                publishedAt
        );

        betEventPublisher.publish(event);

        return publishedAt;
    }
}