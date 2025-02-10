package com.example.project.repository;

import com.example.project.entity.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DefenseEvaluationRepository extends JpaRepository<DefenseEvaluation, String> {

    DefenseEvaluation findByDefenseInstructorIdAndProjectIdAndStudentDefense(ProjectInstructorRole defenseInstructorId, Project projectId, Student studentDefense);
}
