package com.example.project.repository;

import com.example.project.DTO.DefenseEvaResponseDTO;
import com.example.project.entity.DefenseEvalScore;
import com.example.project.entity.ProposalEvalScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DefenseEvalScoreRepository extends JpaRepository<DefenseEvalScore, String> {

    DefenseEvalScore findByEvalId(String s);

    // สำหรับการค้นหาข้อมูลที่ return ค่า DTO
    @Query("SELECT new com.example.project.DTO.DefenseEvaResponseDTO( " +
            "d.defenseEvaluation.defenseEvaId, " +
            "d.defenseEvaluation.student.studentId, " +
            "d.defenseEvaluation.student.studentName, " +
            "d.criteria.criteriaId, " +
            "d.criteria.criteriaName, " +
            "d.criteria.type, " +
            "CAST(d.score AS double)) " +
            "FROM DefenseEvalScore d " +
            "JOIN d.defenseEvaluation.student s " +
            "JOIN d.criteria c " +
            "WHERE d.defenseEvaluation.projectId.projectId = :projectId")
    List<DefenseEvaResponseDTO> findDefenseEvaluationDetailsByProjectId(@Param("projectId") String projectId);


    // สำหรับการค้นหาข้อมูลที่ return เป็น Entity
    List<DefenseEvalScore> findByDefenseEvaluation_ProjectId_ProjectId(String projectId);
}
