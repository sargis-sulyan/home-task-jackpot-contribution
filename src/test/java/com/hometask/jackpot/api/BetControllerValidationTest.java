package com.hometask.jackpot.api;

import com.hometask.jackpot.application.BetApplicationService;
import com.hometask.jackpot.application.reward.JackpotRewardEvaluationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BetController.class)
class BetControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BetApplicationService betApplicationService;

    @MockitoBean
    private JackpotRewardEvaluationService jackpotRewardEvaluationService;

    @Test
    void shouldReturnBadRequestWithValidationErrorsForInvalidCreateBetRequest() throws Exception {
        // Given
        String requestBody = """
                {
                  "betId": "",
                  "userId": "",
                  "jackpotId": "",
                  "betAmount": -1
                }
                """;

        // When / Then
        mockMvc.perform(post("/api/v1/bets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/api/v1/bets"))
                .andExpect(jsonPath("$.validationErrors.betId").exists())
                .andExpect(jsonPath("$.validationErrors.userId").exists())
                .andExpect(jsonPath("$.validationErrors.jackpotId").exists())
                .andExpect(jsonPath("$.validationErrors.betAmount").exists());
    }

    @TestConfiguration
    static class ClockTestConfiguration {

        @Bean
        Clock clock() {
            return Clock.fixed(Instant.parse("2026-05-01T10:15:30Z"), ZoneOffset.UTC);
        }
    }
}
