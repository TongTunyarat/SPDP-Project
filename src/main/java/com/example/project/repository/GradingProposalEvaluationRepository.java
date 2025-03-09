package com.example.project.repository;

import com.example.project.entity.GradingProposalEvaluation;
import com.example.project.entity.Project;
import com.example.project.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface GradingProposalEvaluationRepository extends JpaRepository<GradingProposalEvaluation, String> {

    GradingProposalEvaluation findByProjectAndStudent(Project project, Student student);

    List<GradingProposalEvaluation> findByProject_ProjectId(String projectId);

    List<GradingProposalEvaluation> findByProjectIn(Collection<Project> project);

    @Query("SELECT COUNT(DISTINCT g.student) FROM GradingProposalEvaluation g WHERE g.gradeResult IS NOT NULL AND g.gradeResult != 'I' AND g.gradeResult != '' AND g.project IN :projects")
    long countDistinctProjectByProjectIn(@Param("projects") Collection<Project> projects);

    GradingProposalEvaluation findGradeResultByProjectAndStudent_StudentId(Project project, String student);
}
