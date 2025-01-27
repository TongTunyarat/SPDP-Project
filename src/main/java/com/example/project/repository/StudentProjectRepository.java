package com.example.project.repository;

import com.example.project.entity.StudentProject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentProjectRepository extends JpaRepository<StudentProject, String> {
}
