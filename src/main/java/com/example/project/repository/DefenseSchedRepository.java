package com.example.project.repository;

import com.example.project.entity.DefenseSchedule;
import com.example.project.entity.ProposalSchedule;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DefenseSchedRepository extends JpaRepository<DefenseSchedule, String> {

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM DefenseSchedule p WHERE p.projectId IN :projectIds")
    boolean existsByProjectId(@Param("projectIds") List<String> projectIds);

    @Modifying
    @Transactional
    @Query("DELETE FROM DefenseSchedule p WHERE p.projectId IN :projectIds")
    int deleteAllByProgram(@Param("projectIds") List<String> projectIds);

    @Query("SELECT p FROM DefenseSchedule p WHERE p.projectId IN :projectIds AND p.remark = 'Auto-generated schedule'")
    List<DefenseSchedule> findByProjectIds(@Param("projectIds") List<String> projectIds);

    @Query("SELECT p FROM DefenseSchedule p WHERE p.projectId IN :projectId AND p.remark = 'Auto-generated schedule' ")
    DefenseSchedule findByProjectId(String projectId);

    @Query("SELECT p FROM DefenseSchedule p WHERE p.projectId IN :projectIds AND p.remark = 'User-Add' ")
    List<DefenseSchedule> findEditProject(@Param("projectIds") List<String> projectIds);

    @Query("SELECT p FROM DefenseSchedule p WHERE p.projectId IN :projectId AND p.remark = 'User-Add' ")
    List<DefenseSchedule> findEditProjectByProjectId(String projectId);

    @Query("SELECT p FROM DefenseSchedule p WHERE p.projectId IN :projectId AND p.remark = 'User-Add' ")
    DefenseSchedule findEditByProjectId(String projectId);

    @Query("SELECT p FROM DefenseSchedule p WHERE p.status = 'Active' ")
    List<DefenseSchedule> findByAllAndStatusActive();

    @Query("SELECT p FROM DefenseSchedule p WHERE p.projectId IN :projectIds AND p.remark = 'Auto-Ungenerated schedule'")
    List<DefenseSchedule> findByProjectIdsUnschedule(@Param("projectIds") List<String> projectIds);

    @Query("SELECT p FROM DefenseSchedule p WHERE p.projectId IN :projectId ")
    List<DefenseSchedule> findByProjectAllId(String projectId);

    @Modifying
    @Query("""
    DELETE FROM DefenseSchedule ds
    WHERE ds.project.semester = :semester
      AND (:program IS NULL OR LOWER(ds.project.program) = LOWER(:program))
  """)
    int deleteBySemesterAndProgram(
            @Param("semester") String semester,
            @Param("program")  String program);
}