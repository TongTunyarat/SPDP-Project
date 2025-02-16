package com.example.project.repository;

import com.example.project.entity.GradingProposalEvaluation;
import com.example.project.entity.Project;
import com.example.project.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GradingProposalEvaluationRepository extends JpaRepository<GradingProposalEvaluation, String> {

    GradingProposalEvaluation findByProjectAndStudent(Project project, Student student);

    List<GradingProposalEvaluation> findByProject_ProjectId(String projectId);
}
