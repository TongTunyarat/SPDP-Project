package com.example.project.repository;

import com.example.project.entity.Instructor;
import com.example.project.entity.Project;
import com.example.project.entity.ProjectInstructorRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectInstructorRoleRepository extends JpaRepository<ProjectInstructorRole, String> {
    List<ProjectInstructorRole> findByProjectIdRole_ProjectId(String ProjectId);

    // ค้นหาจาก Instructor และ Project ID
    ProjectInstructorRole findByInstructorAndProjectIdRole(Instructor instructor, Project projectIdRole);

    List<ProjectInstructorRole> findByProjectIdRoleAndRole(Project projectIdRole, String role);

    List<ProjectInstructorRole> findByProjectIdRole(Project currentProject);

    List<ProjectInstructorRole> findByProjectIdRole_Semester(String year);

    List<ProjectInstructorRole> findByProjectIdRole_SemesterAndProjectIdRole_Program(String year, String program);

}

