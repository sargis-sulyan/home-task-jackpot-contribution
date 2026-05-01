package com.hometask.jackpot.infrastructure.persistance.repository;

import com.hometask.jackpot.infrastructure.persistance.entity.JackpotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JackpotRepository extends JpaRepository<JackpotEntity, String> {
}