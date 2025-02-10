package com.example.project.repository;


import com.example.project.DTO.FilterResponseDTO;
import com.example.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ProjectRepository extends JpaRepository<Project, Long> {

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

