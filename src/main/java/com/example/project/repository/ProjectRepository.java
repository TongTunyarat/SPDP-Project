package com.example.project.repository;


import com.example.project.DTO.FilterResponseDTO;
import com.example.project.DTO.projectManagement.ProjectDTO;
import com.example.project.DTO.projectManagement.StudentProjectDTO;
import com.example.project.entity.Project;
import com.example.project.entity.ProposalSchedule;
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

    @Query("SELECT MAX(p.projectId) FROM Project p")
    String findLastProjectId();  // ค้นหาค่ารหัสล่าสุด

    List<Project> findBySemester(String param);

    List<Project> findBySemesterAndProgram(String param, String year);

    @Query("SELECT p.projectId FROM Project p WHERE p.program = :program")
    List<String> findByProjectIdAndProgram(@Param("program") String program);

    @Query("SELECT p.projectId FROM Project p")
    List<String> findByProjectIdList();

    @Query("SELECT p FROM Project p WHERE p.projectId IN :projectIds")
    List<Project> findByProjectIds(@Param("projectIds") List<String> projectIds);

    @Query("SELECT DISTINCT p FROM Project p " +
            "JOIN p.projectInstructorRoles pir " +
            "JOIN pir.instructor i " +
            "JOIN i.account a " +
            "WHERE a.username = :username " +
            "AND (pir.role = 'Advisor' OR pir.role = 'Committee')")
    List<Project> findProjectsByUsername(@Param("username") String username);

    @Query("SELECT p.projectId FROM Project p WHERE p.program = :program AND p.semester = :semester ")
    List<String> findByProjectIdAndProgramAndMaxSemster(@Param("program") String program, @Param("semester") String semester);

    List<Project> findBySemesterAndProjectInstructorRoles_Instructor_Account_Username(String semester, String username);

    List<Project> findBySemesterAndProgramAndProjectInstructorRoles_Instructor_Account_Username(String param, String year, String username);

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

