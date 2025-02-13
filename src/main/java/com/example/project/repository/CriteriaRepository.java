package com.example.project.repository;

import com.example.project.entity.Criteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CriteriaRepository extends JpaRepository<Criteria, String> {
    List<Criteria> findByEvaluationPhase(String evaluationPhase);
}
