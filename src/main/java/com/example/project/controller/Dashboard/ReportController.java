package com.example.project.controller.Dashboard;

import com.example.project.DTO.Dashboard.*;
import com.example.project.repository.GradingDefenseEvaluationRepository;
import com.example.project.repository.GradingProposalEvaluationRepository;
import com.example.project.repository.ProjectInstructorRoleRepository;
import com.example.project.repository.StudentProjectRepository;
import com.example.project.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class ReportController {

    @Autowired
    private StudentProjectRepository studentProjectRepository;
    @Autowired
    private GradingProposalEvaluationRepository gradingProposalEvaluationRepository;
    @Autowired
    private ProjectInstructorRoleRepository projectInstructorRoleRepository;
    @Autowired
    private GradingDefenseEvaluationRepository gradingDefenseEvaluationRepository;
    @Autowired
    private ReportService reportService;



    @GetMapping("/api/proposal-grade")
    public ResponseEntity<?> getGradingPropStatistics(
            @RequestParam String year
    ) {
        ProposalGradeDTO proposalGradeDTO = new ProposalGradeDTO(
                studentProjectRepository.findByProject_Semester(year),
                gradingProposalEvaluationRepository.findByProject_Semester(year),
                projectInstructorRoleRepository.findByProjectIdRole_Semester(year));
        return ResponseEntity.ok(proposalGradeDTO);
    }

    @GetMapping("/api/defense-grade")
    public ResponseEntity<?> getGradingDefStatistics(
            @RequestParam String year
    ) {
        DefenseGradeDTO defenseGradeDTO = new DefenseGradeDTO(
                studentProjectRepository.findByProject_Semester(year),
                gradingDefenseEvaluationRepository.findByProjectId_Semester(year),
                projectInstructorRoleRepository.findByProjectIdRole_Semester(year)
        );
        return ResponseEntity.ok(defenseGradeDTO);
    }

    @GetMapping("/api/evaluation-tracking")
    public ResponseEntity<?> getGradingStatistics(
            @RequestParam String evaType,
            @RequestParam String year
    ) {
    try {
            if (evaType == null || evaType.isBlank()) {
                return ResponseEntity.badRequest().body("evaType parameter is required");
            }

            System.out.println("Fetching evaluation for type: " + evaType);
            EvaluationTrackingDTO evaStatus = reportService.getEvaluationTracking(evaType, year);

            if (evaStatus == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No data found for program: " + evaType);
            }

            return ResponseEntity.ok(evaStatus);
        } catch (Exception e) {
            System.err.println("Error fetching evaluation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the request");
        }
    }


}
