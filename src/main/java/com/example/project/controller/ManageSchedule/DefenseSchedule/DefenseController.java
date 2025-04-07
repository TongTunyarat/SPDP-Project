package com.example.project.controller.ManageSchedule.DefenseSchedule;

import com.example.project.DTO.ManageSchedule.BookingSettingsDTO;
import com.example.project.DTO.ManageSchedule.Defense.BookingDefenseSettingsDTO;
import com.example.project.DTO.ManageSchedule.Defense.ScheduleDefenseResponseDTO;
import com.example.project.DTO.ManageSchedule.ProjectWithInstructorsDTO;
import com.example.project.DTO.ManageSchedule.ScheduleProposalResponseDTO;
import com.example.project.repository.DefenseSchedRepository;
import com.example.project.repository.ProjectRepository;
import com.example.project.service.ManageSchedule.DefenseSchedule.DefenseScheduleService;
import com.example.project.service.ManageSchedule.DefenseSchedule.ManageDefenseService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DefenseController {

    @Autowired
    DefenseScheduleService defenseScheduleService;
    @Autowired
    ManageDefenseService manageDefenseService;

    @GetMapping("/admin/defenseSchedule")
    public String manageProposalSchedule(Model model, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Account username: " + authentication.getName());
        System.out.println("Session ID: " + RequestContextHolder.currentRequestAttributes().getSessionId());

        return "ManageSchedule/DefenseSchedule";
    }

    // check have project
    @GetMapping("/admin/checkExitsDefenseSchedule")
    @ResponseBody
    public Map<String, Boolean> checkExistingDefenseSchedule() {

        Map<String, Boolean> response = new HashMap<>();
        response.put("checkEexti", defenseScheduleService.haveExitDefenseSchedule());
        return response;
    }

    // delete all
    @GetMapping("/admin/deleteAllExitsDefenseSchedule")
    @ResponseBody
    public Map<String,Boolean> deleteAllProposalSchedule() {
        System.out.println("❗️ Delete exist schedules");

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleteStatus", manageDefenseService.deleteAllDefenseSchedule());
        return response;
    }

    @PostMapping ("/admin/bookingSaveDefense")
    public ResponseEntity<ScheduleDefenseResponseDTO> saveBookingDefenseSettings(@RequestBody BookingDefenseSettingsDTO settingData) {
        System.out.println("💌 Received Booking Data:");
        System.out.println("Start Date: " + settingData.getStartDate());
        System.out.println("End Date: " + settingData.getEndDate());

        String startDate = settingData.getStartDate();
        String endDate = settingData.getEndDate();

        List<ProjectWithInstructorsDTO> projectWithInstructorsDTOList = defenseScheduleService.prepareDefenseProject();

        if(projectWithInstructorsDTOList.isEmpty()) {
            return ResponseEntity.badRequest().body(new ScheduleDefenseResponseDTO("error", "There are no project entries in the database"));
        }

        ScheduleDefenseResponseDTO scheduleStatus  = defenseScheduleService.generateDefenseSchedule(startDate, endDate, projectWithInstructorsDTOList);

        if(scheduleStatus.getStatus().equals("error")) {
            return ResponseEntity.badRequest().body(scheduleStatus);
        }

        return ResponseEntity.ok(scheduleStatus);
    }



}
