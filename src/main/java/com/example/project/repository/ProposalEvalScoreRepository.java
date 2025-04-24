package com.example.project.repository;

import com.example.project.entity.ProposalEvalScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProposalEvalScoreRepository extends JpaRepository<ProposalEvalScore, String> {
    ProposalEvalScore findByEvaId(String s);

    ProposalEvalScore findByEvaIdAndCriteria_CriteriaId(String s, String s1);

    ProposalEvalScore findByProposalEvaluation_ProposalIdAndCriteria_CriteriaId(String s, String s1);

    List<ProposalEvalScore> findByProposalEvaluation_Project_ProjectId(String projectId);

    @Modifying
    @Query("""
    DELETE FROM ProposalEvalScore pes
    WHERE pes.proposalEvaluation.project.semester = :semester
      AND (:program IS NULL OR LOWER(pes.proposalEvaluation.project.program) = LOWER(:program))
  """)
    int deleteBySemesterAndProgram(@Param("semester") String semester,
                                   @Param("program")  String program);
}

