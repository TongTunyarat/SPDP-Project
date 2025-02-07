package com.example.project.repository;

import com.example.project.entity.ProjectInstructorRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectInstructorRoleRepository extends JpaRepository<ProjectInstructorRole, String> {
    List<ProjectInstructorRole> findByProjectIdRole_ProjectId(String ProjectId);
}
