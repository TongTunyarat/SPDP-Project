package com.example.project.repository;

import com.example.project.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PosterEvaRepository extends JpaRepository<PosterEvaluation, String> {

    PosterEvaluation findByInstructorIdPosterAndProjectIdPoster(ProjectInstructorRole instructorIdPoster, Project projectIdPoster);
}
