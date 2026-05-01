package com.hometask.jackpot.domain.strategy.reward;

import java.math.BigDecimal;

public interface RandomPercentageGenerator {

    BigDecimal nextPercentage();
}