package com.example.project.repository;

import com.example.project.entity.StudentProject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface StudentProjectRepository extends JpaRepository<StudentProject, String> {
    List<StudentProject> findByProject_ProjectId(String projectProjectId);
}
