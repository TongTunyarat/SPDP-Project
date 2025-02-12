package com.example.project.repository;

import com.example.project.entity.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DefenseEvaluationRepository extends JpaRepository<DefenseEvaluation, String> {

    DefenseEvaluation findByDefenseInstructorIdAndProjectIdAndStudentDefense(ProjectInstructorRole defenseInstructorId, Project projectId, Student studentDefense);

//    DefenseEvaluation findByProjectIdAndStudentDefense(Project project, Student student);

    List<DefenseEvaluation> findByProjectId(Project project);

    List<DefenseEvaluation> findByProjectIdAndStudentDefense(Project projectId, Student studentDefense);
}
