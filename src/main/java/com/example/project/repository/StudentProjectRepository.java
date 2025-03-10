package com.example.project.repository;

import com.example.project.entity.StudentProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentProjectRepository extends JpaRepository<StudentProject, String> {

    List<StudentProject> findByProject_ProjectId(String projectId);

    Optional<StudentProject> findByProject_ProjectIdAndStudentStudentId(String projectId, String studentId);

}
