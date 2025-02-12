package com.example.project.repository;

import com.example.project.entity.GradingProposalEvaluation;
import com.example.project.entity.Project;
import com.example.project.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradingProposalEvaluationRepository extends JpaRepository<GradingProposalEvaluation, String> {

    GradingProposalEvaluation findByProjectAndStudent(Project project, Student student);
}
