package com.example.project.controller.Dashboard;

import com.example.project.DTO.Dashboard.GradingStatisticsDTO;
import com.example.project.DTO.Dashboard.EvaluationStatusResponse;
import com.example.project.entity.ScoringPeriods;
import com.example.project.service.ScoringPeriodsService;
import com.example.project.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;


    // Get Year to Select
    @GetMapping("/api/academic-years")
    public ResponseEntity<List<String>> getAcademicYears() {
        List<String> academicYears = statisticsService.getAllAcademicYears();
        return ResponseEntity.ok(academicYears);
    }

    // Get Grading [ gradeCompleted / totalStudent]
    @GetMapping("/api/grading-statistics")
    public ResponseEntity<GradingStatisticsDTO> getGradingStatistics(@RequestParam String year) {
        System.out.println("📆 year: " + year);
        GradingStatisticsDTO statistics = statisticsService.getGradingStatistics(year);
        return ResponseEntity.ok(statistics);
    }

    // Get Proposal Evaluation
    @GetMapping("/api/proposal-evaluation")
    public ResponseEntity<?> getProposalEvaluationStatus(
            @RequestParam String program,
            @RequestParam String year
    ){
        System.out.println("program [proposal]: " + program);
        System.out.println("year [proposal]: " + year);

        try {
            if (program == null || program.isBlank()) {
                return ResponseEntity.badRequest().body("Program parameter is required");
            }

            System.out.println("Fetching proposal evaluation for program: " + program);
            EvaluationStatusResponse status = statisticsService.getProposalEvaluationStatus(program, year);

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

    // Get Defense Evaluation
    @GetMapping("/api/defense-evaluation")
    public ResponseEntity<?> getDefenseEvaluationStatus(
            @RequestParam String program,
            @RequestParam String year
    ){
        System.out.println("program [defense]: " + program);
        System.out.println("year [defense]: " + year);

        try {
            if (program == null || program.isBlank()) {
                return ResponseEntity.badRequest().body("Program parameter is required");
            }

            System.out.println("Fetching defense evaluation for program: " + program);
            EvaluationStatusResponse status = statisticsService.getDefenseEvaluationStatus(program, year);

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

    // Get Poster Evaluation
    @GetMapping("/api/poster-evaluation")
    public ResponseEntity<?> getPosterEvaluationStatus(
            @RequestParam String program,
            @RequestParam String year
    ){
        System.out.println("program [poster]: " + program);
        System.out.println("year [poster]: " + year);

        try {
            if (program == null || program.isBlank()) {
                return ResponseEntity.badRequest().body("Program parameter is required");
            }

            System.out.println("Fetching poster evaluation for program: " + program);
            EvaluationStatusResponse status = statisticsService.getPosterEvaluationStatus(program, year);

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

    // Get Grading Statistic for graph
    @GetMapping("/api/grading-graph")
    public ResponseEntity<?> getGradingStatistics(
            @RequestParam String program,
            @RequestParam String year,
            @RequestParam String evaType
    ){
        try {
            if (program == null || program.isBlank()) {
                return ResponseEntity.badRequest().body("Program parameter is required");
            }

            System.out.println("Fetching grading for program: " + program + " and year: " + year + " and evaType: " + evaType);
            Map<String, Integer> gradeDistribution = statisticsService.getGradeDistribution(program, year, evaType);

            if (gradeDistribution == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No data found for program: " + program);
            }

            return ResponseEntity.ok(gradeDistribution);
        } catch (Exception e) {
            System.err.println("Error fetching grade: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the request");
        }
    }

    @GetMapping("/api/important-dates")
    public ResponseEntity<List<ScoringPeriods>> getImportantDates() {
        List<ScoringPeriods> importantDates = statisticsService.getAllScoringPeriods();
        return ResponseEntity.ok(importantDates);
    }
}
