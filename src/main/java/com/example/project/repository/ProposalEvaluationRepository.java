package com.example.project.repository;

import com.example.project.entity.ProposalEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProposalEvaluationRepository extends JpaRepository<ProposalEvaluation, String> {
    List<ProposalEvaluation> findByProject_ProjectId(String projectId);
}
