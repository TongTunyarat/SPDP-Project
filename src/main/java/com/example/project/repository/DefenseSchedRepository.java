package com.example.project.repository;

import com.example.project.entity.DefenseSchedule;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DefenseSchedRepository extends JpaRepository<DefenseSchedule, String> {

    @Modifying
    @Transactional
    @Query("DELETE FROM DefenseSchedule p WHERE p.projectId IN :projectIds")
    int deleteAllByProgram(@Param("projectIds") List<String> projectIds);

}
