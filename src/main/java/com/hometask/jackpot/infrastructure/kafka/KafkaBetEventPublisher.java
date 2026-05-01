package com.hometask.jackpot.infrastructure.kafka;

import com.hometask.jackpot.application.event.BetEventPublisher;
import com.hometask.jackpot.application.event.BetPlacedEvent;
import com.hometask.jackpot.domain.exception.BetPublishingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class KafkaBetEventPublisher implements BetEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaBetEventPublisher.class);

    private final KafkaTemplate<String, BetPlacedEvent> kafkaTemplate;
    private final String topicName;

    public KafkaBetEventPublisher(
            KafkaTemplate<String, BetPlacedEvent> kafkaTemplate,
            @Value("${spring.kafka.topic.jackpot-bets}") String topicName
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    @Override
    public void publish(BetPlacedEvent event) {
        try {
            kafkaTemplate
                    .send(topicName, event.betId(), event)
                    .get(5, TimeUnit.SECONDS);

            log.info(
                    "Bet event published: betId={}, userId={}, jackpotId={}, amount={}",
                    event.betId(),
                    event.userId(),
                    event.jackpotId(),
                    event.betAmount()
            );
        } catch (Exception ex) {
            throw new BetPublishingException(event.betId(), ex);
        }
    }
}