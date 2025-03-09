package com.example.project.repository;

import com.example.project.entity.*;
import com.example.project.service.DefenseEvaluationService;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DefenseEvaluationRepository extends JpaRepository<DefenseEvaluation, String> {

    DefenseEvaluation findByDefenseInstructorIdAndProjectIdAndStudent(ProjectInstructorRole defenseInstructorId, Project projectId, Student studentDefense);
//
//    List<DefenseEvaluation> findByProjectId(Project project);
//
//    List<DefenseEvaluation> findByProjectIdAndStudent(Project projectId, Student studentDefense);

    List<DefenseEvaluation> findByProjectId_ProjectId(String projectId);

    int countByDefenseInstructorIdAndProjectId(ProjectInstructorRole instructorRole, Project project);
}
