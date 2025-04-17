package com.example.project.controller.ManageSchedule.DefenseSchedule;

import com.example.project.DTO.ManageSchedule.*;
import com.example.project.DTO.ManageSchedule.Defense.BookingDefenseSettingsDTO;
import com.example.project.DTO.ManageSchedule.Defense.ScheduleDefenseResponseDTO;
import com.example.project.DTO.ManageSchedule.Defense.TimeEachSlotDTO;
import com.example.project.repository.DefenseSchedRepository;
import com.example.project.repository.ProjectRepository;
import com.example.project.service.ManageSchedule.DefenseSchedule.DefenseScheduleService;
import com.example.project.service.ManageSchedule.DefenseSchedule.ManageDefenseService;
import jakarta.servlet.http.HttpServletRequest;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.LocalTime;
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
    public Map<String, Boolean> checkExistingDefenseSchedule(@RequestParam String semesterYear) {

        Map<String, Boolean> response = new HashMap<>();
        response.put("checkEexti", defenseScheduleService.haveExitDefenseSchedule(semesterYear));
        return response;
    }

    // delete all
    @GetMapping("/admin/deleteAllExitsDefenseSchedule")
    @ResponseBody
    public Map<String,Boolean> deleteAllProposalSchedule(@RequestParam String semesterYear) {
        System.out.println("❗️ Delete exist schedules");

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleteStatus", manageDefenseService.deleteAllDefenseSchedule(semesterYear));
        return response;
    }

    @PostMapping ("/admin/bookingSaveDefense")
    public ResponseEntity<ScheduleDefenseResponseDTO> saveBookingDefenseSettings(@RequestBody BookingDefenseSettingsDTO settingData) {
        System.out.println("💌 Received Booking Data:");
        System.out.println("Start Date: " + settingData.getStartDate());
        System.out.println("End Date: " + settingData.getEndDate());
        System.out.println("semesterYear: " + settingData.getSemesterYear());

        for (Map.Entry<String, List<TimeEachSlotDTO>>  entry : settingData.getTimeSlots().entrySet()) {

            String date = entry.getKey();
            List<TimeEachSlotDTO> slots = entry.getValue();

            for (TimeEachSlotDTO slot : slots) {
                System.out.println("📅 Date: " + date + " 🕒 Start: " + slot.getStart() + ", End: " + slot.getEnd());
            }
        }

        String startDate = settingData.getStartDate();
        String endDate = settingData.getEndDate();
        Map<String, List<TimeEachSlotDTO>> timeSlots = settingData.getTimeSlots();
        String semesterYear = settingData.getSemesterYear();


        List<ProjectWithInstructorsDTO> projectWithInstructorsDTOList = defenseScheduleService.prepareDefenseProject(semesterYear);

        if(projectWithInstructorsDTOList.isEmpty()) {
            return ResponseEntity.badRequest().body(new ScheduleDefenseResponseDTO("error", "There are no project entries in the database"));
        }

        ScheduleDefenseResponseDTO scheduleStatus  = defenseScheduleService.generateDefenseSchedule(startDate, endDate, timeSlots, projectWithInstructorsDTOList);

        if(scheduleStatus.getStatus().equals("error")) {
            return ResponseEntity.badRequest().body(scheduleStatus);
        }

        return ResponseEntity.ok(scheduleStatus);
    }

    // อย่าลืมกลับมา detect ค่า null
    @GetMapping("/admin/getAllDefenseSchedule")
    @ResponseBody
    public Map<String, Map<Pair<LocalTime, LocalTime>, List<GetProposalScheduleDTO>>> getProposalSchedule(@RequestParam String semesterYear) {
        return manageDefenseService.getDefenseSchedule(semesterYear);
    }

    // get unSchedule
    @GetMapping("/admin/getAllDefenseUnSchedule")
    @ResponseBody
    public List<GetProposalUnScheduleDTO> getProposalUnSchedule(@RequestParam String semesterYear) {
        return manageDefenseService.getDefenseUnSchedule(semesterYear);
    }


}