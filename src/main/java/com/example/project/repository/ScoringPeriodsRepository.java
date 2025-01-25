package com.example.project.repository;

import com.example.project.entity.ScoringPeriods;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoringPeriodsRepository extends JpaRepository<ScoringPeriods, Integer> {
}