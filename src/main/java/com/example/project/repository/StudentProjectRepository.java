package com.example.project.repository;

import com.example.project.entity.Project;
import com.example.project.entity.StudentProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface StudentProjectRepository extends JpaRepository<StudentProject, String> {
    List<StudentProject> findByProject_ProjectId(String projectProjectId);

    List<StudentProject> findByProject_Semester(String year);

    List<StudentProject> findByProject_SemesterAndProject_Program(String year, String program);

    int countByProject(Project project);

}
