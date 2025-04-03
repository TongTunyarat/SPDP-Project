package com.example.project.repository;


import com.example.project.DTO.FilterResponseDTO;
import com.example.project.entity.Project;
import com.example.project.entity.ProposalSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    List<Project> findBySemester(String param);

    List<Project> findBySemesterAndProgram(String param, String year);

    @Query("SELECT p.projectId FROM Project p WHERE p.program = :program")
    List<String> findByProjectIdAndProgram(@Param("program") String program);

    @Query("SELECT p FROM Project p WHERE p.projectId IN :projectIds")
    List<Project> findByProjectIds(@Param("projectIds") List<String> projectIds);

    @Query("SELECT DISTINCT p FROM Project p " +
            "JOIN p.projectInstructorRoles pir " +
            "JOIN pir.instructor i " +
            "JOIN i.account a " +
            "WHERE a.username = :username " +
            "AND (pir.role = 'Advisor' OR pir.role = 'Committee')")
    List<Project> findProjectsByUsername(@Param("username") String username);


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

