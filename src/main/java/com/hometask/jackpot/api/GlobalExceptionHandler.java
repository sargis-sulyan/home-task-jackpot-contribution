package com.hometask.jackpot.api;

import com.hometask.jackpot.api.dto.ApiErrorResponse;
import com.hometask.jackpot.domain.exception.BetPublishingException;
import com.hometask.jackpot.domain.exception.ContributionNotFoundException;
import com.hometask.jackpot.domain.exception.DuplicateBetException;
import com.hometask.jackpot.domain.exception.JackpotNotFoundException;
import com.hometask.jackpot.domain.exception.UnsupportedJackpotConfigurationException;
import com.hometask.jackpot.domain.exception.RewardNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.KafkaException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final Clock clock;

    public GlobalExceptionHandler(Clock clock) {
        this.clock = clock;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        Map<String, String> validationErrors = new HashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error ->
                validationErrors.put(error.getField(), error.getDefaultMessage())
        );

        return buildResponse(
                "VALIDATION_FAILED",
                "Request validation failed",
                HttpStatus.BAD_REQUEST,
                request,
                validationErrors
        );
    }

    @ExceptionHandler(JackpotNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleJackpotNotFoundException(
            JackpotNotFoundException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                "JACKPOT_NOT_FOUND",
                exception.getMessage(),
                HttpStatus.NOT_FOUND,
                request,
                null
        );
    }

    @ExceptionHandler(ContributionNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleContributionNotFoundException(
            ContributionNotFoundException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                "CONTRIBUTION_NOT_FOUND",
                exception.getMessage(),
                HttpStatus.NOT_FOUND,
                request,
                null
        );
    }

    @ExceptionHandler(DuplicateBetException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateBetException(
            DuplicateBetException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                "DUPLICATE_BET",
                exception.getMessage(),
                HttpStatus.CONFLICT,
                request,
                null
        );
    }

    @ExceptionHandler(UnsupportedJackpotConfigurationException.class)
    public ResponseEntity<ApiErrorResponse> handleUnsupportedConfigurationException(
            UnsupportedJackpotConfigurationException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                "UNSUPPORTED_JACKPOT_CONFIGURATION",
                exception.getMessage(),
                HttpStatus.UNPROCESSABLE_ENTITY,
                request,
                null
        );
    }

    @ExceptionHandler({BetPublishingException.class, KafkaException.class})
    public ResponseEntity<ApiErrorResponse> handleKafkaException(
            Exception exception,
            HttpServletRequest request
    ) {
        log.error("Kafka operation failed", exception);

        return buildResponse(
                "KAFKA_OPERATION_FAILED",
                "Failed to publish or process Kafka message",
                HttpStatus.SERVICE_UNAVAILABLE,
                request,
                null
        );
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ApiErrorResponse> handleOptimisticLockingException(
            OptimisticLockingFailureException exception,
            HttpServletRequest request
    ) {
        log.warn("Concurrent jackpot update detected", exception);

        return buildResponse(
                "CONCURRENT_JACKPOT_UPDATE",
                "Jackpot was updated concurrently. Please retry the request.",
                HttpStatus.CONFLICT,
                request,
                null
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(
            Exception exception,
            HttpServletRequest request
    ) {
        log.error("Unexpected application error", exception);

        return buildResponse(
                "INTERNAL_SERVER_ERROR",
                "Unexpected application error",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request,
                null
        );
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(
            String code,
            String message,
            HttpStatus status,
            HttpServletRequest request,
            Map<String, String> validationErrors
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                code,
                message,
                status.value(),
                request.getRequestURI(),
                Instant.now(clock),
                validationErrors == null ? Map.of() : validationErrors
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(RewardNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleRewardNotFoundException(
            RewardNotFoundException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                "REWARD_NOT_FOUND",
                exception.getMessage(),
                HttpStatus.NOT_FOUND,
                request,
                null
        );
    }
}