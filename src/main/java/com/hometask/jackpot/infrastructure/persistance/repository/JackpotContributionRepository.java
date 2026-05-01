package com.hometask.jackpot.infrastructure.persistance.repository;

import com.hometask.jackpot.infrastructure.persistance.entity.JackpotContributionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JackpotContributionRepository extends JpaRepository<JackpotContributionEntity, Long> {

    boolean existsByBetId(String betId);

    Optional<JackpotContributionEntity> findByBetId(String betId);
}