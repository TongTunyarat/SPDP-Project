package com.example.project.repository;

import com.example.project.entity.GradingDefenseEvaluation;
import com.example.project.entity.GradingProposalEvaluation;
import com.example.project.entity.Project;
import com.example.project.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradingDefenseEvaluationRepository extends JpaRepository<GradingDefenseEvaluation, String> {
    GradingDefenseEvaluation findByProjectIdAndStudentId(Project project, Student student);
}
