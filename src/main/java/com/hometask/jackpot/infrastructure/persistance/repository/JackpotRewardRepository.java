package com.hometask.jackpot.infrastructure.persistance.repository;

import com.hometask.jackpot.infrastructure.persistance.entity.JackpotRewardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JackpotRewardRepository extends JpaRepository<JackpotRewardEntity, Long> {

    boolean existsByBetId(String betId);

    Optional<JackpotRewardEntity> findByBetId(String betId);
}