package com.hometask.jackpot.infrastructure.persistance.repository;

import com.hometask.jackpot.infrastructure.persistance.entity.BetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BetRepository extends JpaRepository<BetEntity, String> {
}