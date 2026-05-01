package com.hometask.jackpot.api;

import com.hometask.jackpot.api.dto.ContributionResponse;
import com.hometask.jackpot.api.dto.JackpotResponse;
import com.hometask.jackpot.api.dto.RewardResponse;
import com.hometask.jackpot.application.JackpotQueryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class JackpotQueryController {

    private final JackpotQueryService jackpotQueryService;

    public JackpotQueryController(JackpotQueryService jackpotQueryService) {
        this.jackpotQueryService = jackpotQueryService;
    }

    @GetMapping("/jackpots/{jackpotId}")
    public JackpotResponse getJackpot(@PathVariable String jackpotId) {
        return jackpotQueryService.getJackpot(jackpotId);
    }

    @GetMapping("/contributions/{betId}")
    public ContributionResponse getContribution(@PathVariable String betId) {
        return jackpotQueryService.getContribution(betId);
    }

    @GetMapping("/rewards/{betId}")
    public RewardResponse getReward(@PathVariable String betId) {
        return jackpotQueryService.getReward(betId);
    }
}