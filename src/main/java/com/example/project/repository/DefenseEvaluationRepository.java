package com.example.project.repository;

import com.example.project.entity.DefenseEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DefenseEvaluationRepository extends JpaRepository<DefenseEvaluation, String> {
    List<DefenseEvaluation> findByProjectId_ProjectId(String projectId);
}
