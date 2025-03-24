package com.example.project.repository;

import com.example.project.entity.ProposalSchedule;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProposalSchedRepository extends JpaRepository<ProposalSchedule, String> {

    // https://www.interviewquery.com/p/sql-count-case-when
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM ProposalSchedule p WHERE p.projectId IN :projectIds")
    boolean existsByProjectId(@Param("projectIds") List<String> projectIds);

    @Modifying
    @Transactional
    @Query("DELETE FROM ProposalSchedule p WHERE p.projectId IN :projectIds")
    int deleteAllByProgram(@Param("projectIds") List<String> projectIds);

    @Query("SELECT p FROM ProposalSchedule p WHERE p.projectId IN :projectIds")
    List<ProposalSchedule> findByProjectIds(@Param("projectIds") List<String> projectIds);

}
