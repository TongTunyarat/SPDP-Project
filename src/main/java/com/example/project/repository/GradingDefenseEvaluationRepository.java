package com.example.project.repository;

import com.example.project.entity.GradingDefenseEvaluation;
import com.example.project.entity.GradingProposalEvaluation;
import com.example.project.entity.Project;
import com.example.project.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface GradingDefenseEvaluationRepository extends JpaRepository<GradingDefenseEvaluation, String> {
    GradingDefenseEvaluation findByProjectIdAndStudentId(Project project, Student student);

    List<GradingDefenseEvaluation> findByProjectId_ProjectId(String projectId);

    List<GradingDefenseEvaluation> findByProjectIdIn(Collection<Project> projectId);

    @Query("SELECT COUNT(DISTINCT g.studentId) FROM GradingDefenseEvaluation g WHERE g.gradeResult IS NOT NULL AND g.gradeResult != 'I' AND g.gradeResult != '' AND g.projectId IN :projects")
    long countDistinctProjectIdByProjectIdIn(@Param("projects") Collection<Project> projectId);

    GradingDefenseEvaluation findGradeResultByProjectIdAndStudentId_StudentId(Project projectId, String studentId);

    List<GradingDefenseEvaluation> findByProjectId_Semester(String year);

    @Modifying
    @Query("""
      DELETE FROM GradingDefenseEvaluation g
      WHERE g.projectId.semester = :semester
        AND (:program IS NULL OR LOWER(g.projectId.program) = LOWER(:program))
    """)
    int deleteBySemesterAndProgram(
            @Param("semester") String semester,
            @Param("program")  String program
    );

}
