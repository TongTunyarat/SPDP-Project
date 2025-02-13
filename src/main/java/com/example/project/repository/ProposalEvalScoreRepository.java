package com.example.project.repository;

import com.example.project.entity.ProposalEvalScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProposalEvalScoreRepository extends JpaRepository<ProposalEvalScore, String> {
    ProposalEvalScore findByEvaId(String s);

    List<ProposalEvalScore> findByProposalEvaluation_Project_ProjectId(String projectId);
}

