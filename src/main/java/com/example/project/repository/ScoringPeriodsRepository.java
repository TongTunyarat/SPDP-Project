package com.example.project.repository;

import com.example.project.entity.ScoringPeriods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

public interface ScoringPeriodsRepository extends JpaRepository<ScoringPeriods, Integer> {
    Optional<ScoringPeriods> findByEvaluationType(String evaluationType);

    @Query("SELECT s.startDate FROM ScoringPeriods s WHERE s.evaluationType = :evaluationType")
    LocalDate findStartDateByEvaluationType(@Param("evaluationType") String evaluationType);

    @Query("SELECT s.endDate FROM ScoringPeriods s WHERE s.evaluationType = :evaluationType")
    LocalDate findEndDateByEvaluationType(@Param("evaluationType") String evaluationType);
}
