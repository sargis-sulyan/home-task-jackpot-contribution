package com.hometask.jackpot.infrastructure.kafka;

import com.hometask.jackpot.application.contribution.JackpotContributionResult;
import com.hometask.jackpot.application.contribution.JackpotContributionService;
import com.hometask.jackpot.application.ProcessBetCommand;
import com.hometask.jackpot.application.event.BetPlacedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class BetPlacedKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(BetPlacedKafkaConsumer.class);

    private final JackpotContributionService jackpotContributionService;

    public BetPlacedKafkaConsumer(JackpotContributionService jackpotContributionService) {
        this.jackpotContributionService = jackpotContributionService;
    }

    @KafkaListener(
            topics = "${spring.kafka.topic.jackpot-bets}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(@Payload BetPlacedEvent event) {
        log.info(
                "Bet event consumed: betId={}, userId={}, jackpotId={}, amount={}",
                event.betId(),
                event.userId(),
                event.jackpotId(),
                event.betAmount()
        );

        JackpotContributionResult result = jackpotContributionService.contribute(
                new ProcessBetCommand(
                        event.betId(),
                        event.userId(),
                        event.jackpotId(),
                        event.betAmount()
                )
        );

        log.info(
                "Jackpot contribution processed: betId={}, jackpotId={}, contributionAmount={}, currentJackpotAmount={}",
                result.betId(),
                result.jackpotId(),
                result.contributionAmount(),
                result.currentJackpotAmount()
        );
    }
}