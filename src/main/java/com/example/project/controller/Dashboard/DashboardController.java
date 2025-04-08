package com.example.project.controller.Dashboard;

import com.example.project.entity.ScoringPeriods;
import com.example.project.repository.ScoringPeriodsRepository;
import com.example.project.service.DashboardCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Controller
public class DashboardController {

    @Autowired
    private DashboardCardService dashboardService;
    @Autowired
    private ScoringPeriodsRepository scoringPeriodsRepository;


    // Proposal
    @GetMapping("/instructor/proposalStatusDashboard")
    public ResponseEntity<Map<String, Object>> checkGroupEvaStatus() {
        Map<String, Object> response = dashboardService.checkGroupEvaStatus();
        return ResponseEntity.ok(response);
    }

    // Poster
    @GetMapping("/instructor/posterStatusDashboard")
    public ResponseEntity<Map<String, Object>> checkGroupPosterEvaStatus() {
        Map<String, Object> response =dashboardService.checkGroupPosterEvaStatus();
        return ResponseEntity.ok(response);
    }

    // Defense
    @GetMapping("/instructor/defenseStatusDashboard")
    public  ResponseEntity<Map<String, Object>> checkGroupDefenseEvaStatus() {
        Map<String, Object> response = dashboardService.checkGroupDefenseEvaStatus();
        return ResponseEntity.ok(response);
    }

    // Period
    @GetMapping("/api/period")
    public ResponseEntity<?> getScoringPeriod() {
        String currentYear = String.valueOf(LocalDate.now().getYear());
        List<ScoringPeriods> response = scoringPeriodsRepository.findByYear(currentYear);

        if (response == null) {
            return ResponseEntity.notFound().build(); // Handle case when no data is found
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/grading-graph-instructor")
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
            Map<String, Integer> gradeDistribution = dashboardService.getGradeDistribution(program, year, evaType);

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


    //=========================================== NOT USE ===================================================

//    @GetMapping("/instructor/programGroup")
//    @ResponseBody
//    public String getProject(Model model) {
//        List<ProjectInstructorRole> projectInstructorRoleList = projectService.getInstructorProject();
//        Long programDST = projectInstructorRoleList.stream()
//                .filter(i -> "DST".equalsIgnoreCase(i.getProjectIdRole().getProgram())).count();
//        Long programICT = projectInstructorRoleList.stream()
//                .filter(i -> "ICT".equalsIgnoreCase(i.getProjectIdRole().getProgram())).count();
//        System.out.println("📚 Check Program Group");
//
//        model.addAttribute("programDST", programDST);
//        model.addAttribute("programICT", programICT);
//
//        return "DST: " + programDST + " ICT: " + programICT;
//    }
}

