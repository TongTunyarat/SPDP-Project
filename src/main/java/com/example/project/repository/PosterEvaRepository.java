package com.example.project.repository;

import com.example.project.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PosterEvaRepository extends JpaRepository<PosterEvaluation, String> {

    PosterEvaluation findByInstructorIdPosterAndProjectIdPoster(ProjectInstructorRole instructorIdPoster, Project projectIdPoster);

    List<PosterEvaluation> findByProjectIdPoster_ProjectId(String projectId);

    int countByInstructorIdPoster_Instructor_ProfessorIdAndProjectIdPoster(String professorId, Project projectIdPoster);

}