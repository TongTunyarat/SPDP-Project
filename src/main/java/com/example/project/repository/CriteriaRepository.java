package com.example.project.repository;

import com.example.project.entity.Criteria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CriteriaRepository extends JpaRepository<Criteria, String> {
    List<Criteria> findByEvaluationPhase(String evaluationPhase);
}
