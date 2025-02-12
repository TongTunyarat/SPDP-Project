package com.example.project.repository;

import com.example.project.entity.Project;
import com.example.project.entity.ProjectInstructorRole;
import com.example.project.entity.ProposalEvaluation;
import com.example.project.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProposalEvaluationRepository extends JpaRepository<ProposalEvaluation, String> {

    ProposalEvaluation findByProjectInstructorRoleAndProjectAndStudent(ProjectInstructorRole instructor, Project project, Student student);

    List<ProposalEvaluation> findByProjectAndStudent(Project project, Student student);
}
