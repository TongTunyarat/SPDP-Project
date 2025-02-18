package com.example.project.repository;

import com.example.project.entity.PosterEvaluation;
import com.example.project.entity.PosterEvaluationScore;
import com.example.project.entity.ProposalEvalScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.project.DTO.PosterEvaScoreDTO;

import java.util.List;

public interface PosterEvaScoreRepository extends JpaRepository<PosterEvaluationScore, String> {
    PosterEvaluationScore findByPosterEvaId(String posterId);

    List<PosterEvaluationScore> findByPosterEvaluation_ProjectIdPoster_ProjectId(String projectId);

}
