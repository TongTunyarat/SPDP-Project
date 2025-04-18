package com.example.project.controller.Dashboard;

import ch.qos.logback.core.model.Model;
import com.example.project.DTO.ManageSchedule.Preview.PreviewProposalDTO;
import com.example.project.entity.ProjectInstructorRole;
import com.example.project.entity.ScoringPeriods;
import com.example.project.entity.StudentProject;
import com.example.project.repository.ScoringPeriodsRepository;
import com.example.project.service.DashboardCardService;
import com.example.project.service.ProjectService;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class DashboardController {

    @Autowired
    private DashboardCardService dashboardService;
    @Autowired
    private ScoringPeriodsRepository scoringPeriodsRepository;
    @Autowired
    private ProjectService projectService;

    // Proposal
    @GetMapping("/instructor/proposalStatusDashboard")
    public ResponseEntity<Map<String, Object>> checkGroupEvaStatus(@RequestParam("year") String year) {
        System.out.println("🌻 year" + year);
        Map<String, Object> response = dashboardService.checkGroupEvaStatus(year);
        return ResponseEntity.ok(response);
    }

    // Poster
    @GetMapping("/instructor/posterStatusDashboard")
    public ResponseEntity<Map<String, Object>> checkGroupPosterEvaStatus(@RequestParam("year") String year) {
        Map<String, Object> response =dashboardService.checkGroupPosterEvaStatus(year);
        return ResponseEntity.ok(response);
    }

    // Defense
    @GetMapping("/instructor/defenseStatusDashboard")
    public  ResponseEntity<Map<String, Object>> checkGroupDefenseEvaStatus(@RequestParam("year") String year) {
        Map<String, Object> response = dashboardService.checkGroupDefenseEvaStatus(year);
        return ResponseEntity.ok(response);
    }

    // Period
    @GetMapping("/api/period")
    public ResponseEntity<?> getScoringPeriod() {
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        int currentMonth = currentDate.getMonthValue(); // 1-12 (มกราคม = 1)

        // ถ้าเดือน <= 6 (มกราคม - มิถุนายน) ใช้ปีปัจจุบัน -1, ถ้า ก.ค. เป็นต้นไปใช้ปีปัจจุบัน
        String defaultYear = String.valueOf(currentMonth <= 6 ? currentYear - 1 : currentYear);

        List<ScoringPeriods> response = scoringPeriodsRepository.findByYear(defaultYear);

        if (response == null || response.isEmpty()) {
            return ResponseEntity.notFound().build(); // ถ้าไม่พบข้อมูล
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

    // Proposal schedule
    @GetMapping("/instructor/getProposalScheduleDashboard")
    @ResponseBody
    public Map<String, List<PreviewProposalDTO>> getProposalScheduleDashboard(@RequestParam("year") String year) {
        return dashboardService.getProposalSchedule(year);
    }

    // Get Project by Instructor
    @GetMapping("/instructor/getProjectByInstructor")
    @ResponseBody
    public Map<String, Long> getProjectByInstructor (@RequestParam("year") String year){

        System.out.println("🌻 year" + year);
        List<ProjectInstructorRole> projectInstructorRoleList = projectService.getInstructorProject();

        Map<String, Long> counts = dashboardService.getProjectByInstructor(projectInstructorRoleList, year);

        System.out.println("📚 Check Program Group: " + counts);

        return counts;

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
