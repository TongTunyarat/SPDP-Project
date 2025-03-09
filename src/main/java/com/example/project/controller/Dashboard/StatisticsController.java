package com.example.project.controller.Dashboard;

import com.example.project.DTO.Dashboard.GradingStatisticsDTO;
import com.example.project.DTO.Dashboard.EvaluationStatusResponse;
import com.example.project.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class StatisticsController {

    private final StatisticsService gradingStatisticsService;

    @Autowired
    public StatisticsController(StatisticsService gradingStatisticsService) {
        this.gradingStatisticsService = gradingStatisticsService;
    }

    @GetMapping("/api/grading-statistics")
    public ResponseEntity<GradingStatisticsDTO> getGradingStatistics() {
        GradingStatisticsDTO statistics = gradingStatisticsService.getGradingStatistics();
        return ResponseEntity.ok(statistics);
    }

    @PostMapping("/api/proposal-evaluation")
    public ResponseEntity<?> getProposalEvaluationStatus(@RequestParam String program) {
        try {
            if (program == null || program.isBlank()) {
                return ResponseEntity.badRequest().body("Program parameter is required");
            }

            System.out.println("Fetching proposal evaluation for program: " + program);
            EvaluationStatusResponse status = gradingStatisticsService.getProposalEvaluationStatus(program);

            if (status == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No data found for program: " + program);
            }

            return ResponseEntity.ok(status);
        } catch (Exception e) {
            System.err.println("Error fetching proposal evaluation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the request");
        }
    }

    @PostMapping("/api/defense-evaluation")
    public ResponseEntity<?> getDefenseEvaluationStatus(@RequestParam String program) {
        try {
            if (program == null || program.isBlank()) {
                return ResponseEntity.badRequest().body("Program parameter is required");
            }

            System.out.println("Fetching defense evaluation for program: " + program);
            EvaluationStatusResponse status = gradingStatisticsService.getDefenseEvaluationStatus(program);

            if (status == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No data found for program: " + program);
            }

            return ResponseEntity.ok(status);
        } catch (Exception e) {
            System.err.println("Error fetching proposal evaluation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the request");
        }
    }

    @PostMapping("/api/poster-evaluation")
    public ResponseEntity<?> getPosterEvaluationStatus(@RequestParam String program) {
        try {
            if (program == null || program.isBlank()) {
                return ResponseEntity.badRequest().body("Program parameter is required");
            }

            System.out.println("Fetching poster evaluation for program: " + program);
            EvaluationStatusResponse status = gradingStatisticsService.getPosterEvaluationStatus(program);

            if (status == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No data found for program: " + program);
            }

            return ResponseEntity.ok(status);
        } catch (Exception e) {
            System.err.println("Error fetching proposal evaluation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the request");
        }
    }

}
