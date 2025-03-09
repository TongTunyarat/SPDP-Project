package com.example.project.repository;

import com.example.project.entity.GradingProposalEvaluation;
import com.example.project.entity.Project;
import com.example.project.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GradingProposalEvaluationRepository extends JpaRepository<GradingProposalEvaluation, String> {

    GradingProposalEvaluation findByProjectAndStudent(Project project, Student student);

    List<GradingProposalEvaluation> findByProject_ProjectId(String projectId);

    @Query("SELECT COUNT(DISTINCT g.student) FROM GradingProposalEvaluation g WHERE g.gradeResult IS NOT NULL AND g.gradeResult != 'I' AND g.gradeResult != ''")
    long countDistinctProjectIds();

    @Query("SELECT DISTINCT g.gradeResult FROM GradingProposalEvaluation g WHERE g.gradeResult IS NOT NULL AND g.gradeResult != 'I' AND g.gradeResult != ''")
    List<String> findDistinctGrades();
}
