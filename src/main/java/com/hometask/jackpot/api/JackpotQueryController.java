package com.hometask.jackpot.api;

import com.hometask.jackpot.api.dto.ContributionResponse;
import com.hometask.jackpot.api.dto.JackpotResponse;
import com.hometask.jackpot.api.dto.RewardResponse;
import com.hometask.jackpot.application.JackpotQueryService;
import com.hometask.jackpot.application.query.ContributionDetails;
import com.hometask.jackpot.application.query.JackpotDetails;
import com.hometask.jackpot.application.query.RewardDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class JackpotQueryController {

    private final JackpotQueryService jackpotQueryService;

    public JackpotQueryController(JackpotQueryService jackpotQueryService) {
        this.jackpotQueryService = jackpotQueryService;
    }

    @GetMapping("/jackpots/{jackpotId}")
    public JackpotResponse getJackpot(@PathVariable String jackpotId) {
        JackpotDetails jackpot = jackpotQueryService.getJackpot(jackpotId);

        return new JackpotResponse(
                jackpot.jackpotId(),
                jackpot.initialPoolAmount(),
                jackpot.currentPoolAmount(),
                jackpot.contributionType(),
                jackpot.rewardType()
        );
    }

    @GetMapping("/contributions/{betId}")
    public ContributionResponse getContribution(@PathVariable String betId) {
        ContributionDetails contribution = jackpotQueryService.getContribution(betId);

        return new ContributionResponse(
                contribution.betId(),
                contribution.userId(),
                contribution.jackpotId(),
                contribution.stakeAmount(),
                contribution.contributionAmount(),
                contribution.currentJackpotAmount(),
                contribution.rewardEvaluated(),
                contribution.rewardEvaluatedAt(),
                contribution.createdAt()
        );
    }

    @GetMapping("/rewards/{betId}")
    public RewardResponse getReward(@PathVariable String betId) {
        RewardDetails reward = jackpotQueryService.getReward(betId);

        return new RewardResponse(
                reward.betId(),
                reward.userId(),
                reward.jackpotId(),
                reward.jackpotRewardAmount(),
                reward.createdAt()
        );
    }
}
