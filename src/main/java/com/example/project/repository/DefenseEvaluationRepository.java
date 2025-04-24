package com.example.project.repository;

import com.example.project.entity.*;
import com.example.project.service.DefenseEvaluationService;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DefenseEvaluationRepository extends JpaRepository<DefenseEvaluation, String> {

    DefenseEvaluation findByDefenseInstructorIdAndProjectIdAndStudent(ProjectInstructorRole defenseInstructorId, Project projectId, Student studentDefense);

    List<DefenseEvaluation> findByProjectId_ProjectId(String projectId);

    int countByDefenseInstructorIdAndProjectId(ProjectInstructorRole instructorRole, Project project);

    int countByDefenseInstructorId_Instructor_ProfessorIdAndProjectId(String professorId, Project project);


    @Modifying
    @Query("""
    DELETE FROM DefenseEvaluation de
    WHERE de.projectId.semester = :semester
      AND (:program IS NULL OR LOWER(de.projectId.program) = LOWER(:program))
  """)
    int deleteBySemesterAndProgram(@Param("semester") String semester,
                                   @Param("program")  String program);


}
