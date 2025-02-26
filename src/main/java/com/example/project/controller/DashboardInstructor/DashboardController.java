package com.example.project.controller.DashboardInstructor;


import com.example.project.service.DashboardInstructor.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Controller
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;


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
