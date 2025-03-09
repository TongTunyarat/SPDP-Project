package com.example.project.repository;

import com.example.project.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProposalEvaluationRepository extends JpaRepository<ProposalEvaluation, String> {

    ProposalEvaluation findByProjectInstructorRoleAndProjectAndStudent(ProjectInstructorRole instructor, Project project, Student student);

    List<ProposalEvaluation> findByProjectAndStudent(Project project, Student student);

    List<ProposalEvaluation> findByProject_ProjectId(String projectId);

    int countByProjectInstructorRoleAndProject(ProjectInstructorRole projectInstructorRole, Project project);
}
