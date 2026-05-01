package com.hometask.jackpot.api;

import com.hometask.jackpot.api.dto.BetPublishResponse;
import com.hometask.jackpot.api.dto.CreateBetRequest;
import com.hometask.jackpot.api.dto.RewardEvaluationResponse;
import com.hometask.jackpot.application.BetApplicationService;
import com.hometask.jackpot.application.reward.JackpotRewardEvaluationResult;
import com.hometask.jackpot.application.reward.JackpotRewardEvaluationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/bets")
public class BetController {

    private static final String PUBLISHED = "PUBLISHED";

    private final BetApplicationService betApplicationService;
    private final JackpotRewardEvaluationService jackpotRewardEvaluationService;

    public BetController(
            BetApplicationService betApplicationService,
            JackpotRewardEvaluationService jackpotRewardEvaluationService
    ) {
        this.betApplicationService = betApplicationService;
        this.jackpotRewardEvaluationService = jackpotRewardEvaluationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public BetPublishResponse publishBet(@Valid @RequestBody CreateBetRequest request) {
        Instant publishedAt = betApplicationService.publishBet(
                request.betId(),
                request.userId(),
                request.jackpotId(),
                request.betAmount()
        );

        return new BetPublishResponse(
                request.betId(),
                PUBLISHED,
                publishedAt
        );
    }

    @PostMapping("/{betId}/reward-evaluation")
    public RewardEvaluationResponse evaluateReward(@PathVariable String betId) {
        JackpotRewardEvaluationResult result = jackpotRewardEvaluationService.evaluate(betId);

        return new RewardEvaluationResponse(
                result.betId(),
                result.userId(),
                result.jackpotId(),
                result.won(),
                result.rewardAmount(),
                result.currentJackpotAmount(),
                result.winningChancePercentage(),
                result.evaluatedAt()
        );
    }
}
