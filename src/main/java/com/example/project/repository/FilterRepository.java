package com.example.project.repository;

import com.example.project.DTO.FilterResponseDTO;
import com.example.project.entity.Project;
import com.example.project.entity.Student;
import com.example.project.entity.ProjectInstructorRole;
import com.example.project.entity.StudentProject;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FilterRepository extends JpaRepository<Project, Long> {

    @Query("SELECT new com.example.project.DTO.FilterResponseDTO(" +
            "s.studentId, s.studentName, p.program, pir.role, p.projectId, p.projectTitle) " +
            "FROM StudentProject sp " +
            "JOIN sp.student s " +
            "JOIN sp.project p " +
            "JOIN ProjectInstructorRole pir ON pir.projectIdRole.projectId = p.projectId " +
            "WHERE (:program IS NULL OR p.program = :program) " +
            "AND (:role IS NULL OR pir.role = :role) ")

    List<FilterResponseDTO> findFilteredData(
            @Param("program") String program,
            @Param("role") String role
    );

}
