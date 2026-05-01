package com.hometask.jackpot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerConfig.class);

    @Bean
    public DefaultErrorHandler kafkaDefaultErrorHandler() {
        FixedBackOff backOff = new FixedBackOff(1_000L, 3L);

        return new DefaultErrorHandler((consumerRecord, exception) -> {
            log.error(
                    "Kafka record processing failed after retries. topic={}, partition={}, offset={}, key={}",
                    consumerRecord.topic(),
                    consumerRecord.partition(),
                    consumerRecord.offset(),
                    consumerRecord.key(),
                    exception
            );
        }, backOff);
    }
}