package com.example.project.repository;

import com.example.project.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProposalEvaluationRepository extends JpaRepository<ProposalEvaluation, String> {

    ProposalEvaluation findByProjectInstructorRoleAndProjectAndStudent(ProjectInstructorRole instructor, Project project, Student student);

    List<ProposalEvaluation> findByProject_ProjectId(String projectId);

    int countByProjectInstructorRoleAndProject(ProjectInstructorRole projectInstructorRole, Project project);

    int countByProjectInstructorRole_Instructor_ProfessorIdAndProjectInstructorRole_ProjectIdRole(String professorId, Project project);
}
