package com.example.project.repository;

import com.example.project.entity.PosterEvaluation;
import com.example.project.entity.PosterEvaluationScore;
import com.example.project.entity.ProposalEvalScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.project.DTO.PosterEvaScoreDTO;

import java.util.List;

public interface PosterEvaScoreRepository extends JpaRepository<PosterEvaluationScore, String> {
    PosterEvaluationScore findByPosterEvaId(String posterId);

    List<PosterEvaluationScore> findByPosterEvaluation_ProjectIdPoster_ProjectId(String projectId);

    @Modifying
    @Query("""
    DELETE FROM PosterEvaluationScore pos
    WHERE pos.posterEvaluation.projectIdPoster.semester = :semester
      AND (:program IS NULL OR LOWER(pos.posterEvaluation.projectIdPoster.program) = LOWER(:program))
  """)
    int deleteBySemesterAndProgram(@Param("semester") String semester,
                                   @Param("program")  String program);

}
