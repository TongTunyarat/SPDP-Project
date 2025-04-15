package com.example.project.repository;

import com.example.project.entity.Instructor;
import com.example.project.entity.Project;
import com.example.project.entity.ProjectInstructorRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectInstructorRoleRepository extends JpaRepository<ProjectInstructorRole, String> {
    List<ProjectInstructorRole> findByProjectIdRole_ProjectId(String ProjectId);

    // ค้นหาจาก Instructor และ Project ID
    ProjectInstructorRole findByInstructorAndProjectIdRole(Instructor instructor, Project projectIdRole);

    List<ProjectInstructorRole> findByProjectIdRoleAndRole(Project projectIdRole, String role);

//    @Query("SELECT pr.instructorId FROM ProjectInstructorRole pr ORDER BY pr.instructorId DESC")
//    String findLatestInstructorId();

    Optional<ProjectInstructorRole> findByProjectIdRole_ProjectIdAndInstructor_ProfessorId(String projectId, String professorId);

//    @Query("SELECT MAX(s.studentPjId) FROM StudentProject s")
//    String findLatestStudentPjId();  // ค้นหาค่ารหัสล่าสุด

    @Query("SELECT MAX(pr.instructorId) FROM ProjectInstructorRole pr")
    String findLatestInstructorId();

    boolean existsByInstructorProfessorId(String instructorId);



    List<ProjectInstructorRole> findByProjectIdRole(Project currentProject);

    List<ProjectInstructorRole> findByProjectIdRole_Semester(String year);

    List<ProjectInstructorRole> findByProjectIdRole_SemesterAndProjectIdRole_Program(String year, String program);

    boolean existsByProjectIdRole_ProjectIdAndInstructor_ProfessorId(String projectId, String professorId);


    boolean existsByProjectIdRole_ProjectIdAndInstructor_ProfessorIdAndRole(String currentProjectId, String professorId, String committee);
}

