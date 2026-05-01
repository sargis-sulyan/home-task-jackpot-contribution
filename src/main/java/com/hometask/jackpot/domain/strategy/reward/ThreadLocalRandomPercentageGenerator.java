package com.hometask.jackpot.domain.strategy.reward;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class ThreadLocalRandomPercentageGenerator implements RandomPercentageGenerator {

    @Override
    public BigDecimal nextPercentage() {
        return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(0, 100))
                .setScale(4, RoundingMode.HALF_UP);
    }
}