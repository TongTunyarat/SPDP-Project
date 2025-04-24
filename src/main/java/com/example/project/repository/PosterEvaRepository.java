package com.example.project.repository;

import com.example.project.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PosterEvaRepository extends JpaRepository<PosterEvaluation, String> {

    PosterEvaluation findByInstructorIdPosterAndProjectIdPoster(ProjectInstructorRole instructorIdPoster, Project projectIdPoster);

    List<PosterEvaluation> findByProjectIdPoster_ProjectId(String projectId);

    int countByInstructorIdPoster_Instructor_ProfessorIdAndProjectIdPoster(String professorId, Project projectIdPoster);

    int countByInstructorIdPosterAndProjectIdPoster(ProjectInstructorRole instructorIdPoster, Project projectIdPoster);

    @Modifying
    @Query("""
    DELETE FROM PosterEvaluation po
    WHERE po.projectIdPoster.semester = :semester
      AND (:program IS NULL OR LOWER(po.projectIdPoster.program) = LOWER(:program))
  """)
    int deleteBySemesterAndProgram(@Param("semester") String semester,
                                   @Param("program")  String program);

}