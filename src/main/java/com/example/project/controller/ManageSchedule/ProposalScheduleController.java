package com.example.project.controller.ManageSchedule;

import com.example.project.DTO.ManageSchedule.BookingSettingsDTO;
import com.example.project.DTO.ManageSchedule.ProjectWithInstructorsDTO;
import com.example.project.entity.ProjectInstructorRole;
import com.example.project.entity.Room;
import com.example.project.service.ManageSchedule.ProposalScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ProposalScheduleController {

    @Autowired
    private ProposalScheduleService proposalScheduleService;


    @GetMapping("/admin/home")
    public String defautlAdmin(Model model, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Account username: " + authentication.getName());
        System.out.println("Session ID: " + RequestContextHolder.currentRequestAttributes().getSessionId());

        // get CSRF จาก request attribute
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
//        model.addAttribute("_csrf", csrfToken);

        if (csrfToken != null) {
            System.out.println("💌 CSRF Token: " + csrfToken.getToken());
        } else {
            System.out.println("CSRF Token is null ❗️");
        }

        return "ManageSchedule/ProposalSchedule";
    }

    // get room
    @GetMapping("/admin/roomSetting")
    @ResponseBody
    public List<String> getRoomSetting() {
        return proposalScheduleService.getRoom();
    }

    // get data from setting button
    @PostMapping("/admin/bookingSave")
    public ResponseEntity<Map<String, Object>> saveBookingSettings(@RequestBody BookingSettingsDTO settingData) {
        System.out.println("💌 Received Booking Data:");
        System.out.println("Start Date: " + settingData.getStartDate());
        System.out.println("End Date: " + settingData.getEndDate());
        System.out.println("Start Time: " + settingData.getStartTime());
        System.out.println("End Time: " + settingData.getEndTime());
        System.out.println("Rooms: " + settingData.getRooms());
        System.out.println("Program: " + settingData.getProgram());


        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Presentation schedule has been received.");
        response.put("receivedData", settingData);

        String startDate = settingData.getStartDate();
        String endDate = settingData.getEndDate();
        String startTime = settingData.getStartTime();
        String endTime = settingData.getEndTime();
        List<String> roomNumbers =  settingData.getRooms();
        String program = settingData.getProgram();

        List<ProjectWithInstructorsDTO> projectWithInstructorsDTOList = proposalScheduleService.prepareProject(program);

        proposalScheduleService.generateSchedule(startDate, endDate, startTime, endTime, roomNumbers, projectWithInstructorsDTOList);


        return ResponseEntity.ok(response);
    }

    // check project
    @GetMapping("/see")
    @ResponseBody
    public List<ProjectWithInstructorsDTO> prepareProject(String program) {
        return proposalScheduleService.prepareProject(program);
    }

}
