package com.example.project.repository;


import com.example.project.DTO.FilterResponseDTO;
import com.example.project.DTO.projectManagement.ProjectDTO;
import com.example.project.DTO.projectManagement.StudentProjectDTO;
import com.example.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {

    @Query("SELECT p FROM Project p WHERE " +
            "LOWER(p.projectId) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.program) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.semester) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.projectCategory) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.projectTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.projectDescription) LIKE LOWER(CONCAT('%', :keyword, '%'))")

    List<Project> searchProjects(@Param("keyword") String keyword);

    Object[] findProfessorNameByProjectId(@Param("projectId") String projectId);

    Project findByProjectId(String projectId);

    @Query("SELECT new com.example.project.DTO.projectManagement.ProjectDTO( " +
            "p.projectId, p.program, p.semester, p.projectTitle, " +
            "p.projectCategory, p.projectDescription, pir.role, i.professorName) " +
            "FROM Project p " +
            "LEFT JOIN ProjectInstructorRole pir ON p.projectId = pir.projectIdRole.projectId " +
            "LEFT JOIN pir.instructor i")
    List<ProjectDTO> findAllProjectsWithRolesAndInstructor();



}


// Default กัน พลาด เสียหาย
//public interface ProjectRepository extends JpaRepository<Project, Long> {
//
//    @Query("SELECT p FROM Project p WHERE " +
//            "LOWER(p.projectId) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//            "LOWER(p.program) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//            "LOWER(p.semester) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//            "LOWER(p.projectCategory) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//            "LOWER(p.projectTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//            "LOWER(p.projectDescription) LIKE LOWER(CONCAT('%', :keyword, '%'))")
//    List<Project> searchProjects(@Param("keyword") String keyword);
//
//    Project findByProjectId(String projectId);
//
//}

