package com.example.project.controller.ManageSchedule;

import com.example.project.DTO.ManageSchedule.*;
import com.example.project.DTO.ManageSchedule.Preview.PreviewProposalDTO;
import com.example.project.entity.Project;
import com.example.project.entity.ProjectInstructorRole;
import com.example.project.entity.ProposalSchedule;
import com.example.project.entity.Room;
import com.example.project.service.ManageSchedule.ManageProposalScheduleService;
import com.example.project.service.ManageSchedule.ProposalScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import org.antlr.v4.runtime.misc.Pair;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;

import javax.swing.text.html.parser.Entity;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ProposalScheduleController {

    @Autowired
    private ProposalScheduleService proposalScheduleService;
    @Autowired
    private ManageProposalScheduleService manageProposalScheduleService;


    @GetMapping("/admin/proposalSchedule")
    public String manageProposalSchedule(Model model, HttpServletRequest request) {
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

    @GetMapping("/admin/roomFloorSetting")
    @ResponseBody
    public Map<String, String> getRoomFloorSetting() {
        return proposalScheduleService.getRoomWithFloor();
    }

    // check have project
    @GetMapping("/admin/checkExitsSchedule")
    @ResponseBody
    public Map<String, Boolean> checkExistingSchedule(@RequestParam String program, @RequestParam String semesterYear) {
        System.out.println("💌 Check exist schedules for program: " + program + " And semester " + semesterYear);

        Map<String, Boolean> response = new HashMap<>();
        response.put("checkEexti", proposalScheduleService.haveExitSchedule(program, semesterYear));
        return response;
    }

    // delete all
    @GetMapping("/admin/deleteAllExitsSchedule")
    @ResponseBody
    public Map<String,Boolean> deleteAllProposalSchedule(@RequestParam String program, @RequestParam String semesterYear) {
        System.out.println("❗️ Delete exist schedules for program: " + program + "And semester " + semesterYear);

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleteStatus", manageProposalScheduleService.deleteAllProposalSchedule(program, semesterYear));
        return response;
    }

    @PostMapping("/admin/bookingSave")
    public ResponseEntity<ScheduleProposalResponseDTO> saveBookingSettings(@RequestBody BookingSettingsDTO settingData) {
        System.out.println("💌 Received Booking Data:");
        System.out.println("Start Date: " + settingData.getStartDate());
        System.out.println("End Date: " + settingData.getEndDate());
        System.out.println("Start Time: " + settingData.getStartTime());
        System.out.println("End Time: " + settingData.getEndTime());
        System.out.println("Rooms: " + settingData.getRooms());
        System.out.println("Program: " + settingData.getProgram());
        System.out.println("Semester: " + settingData.getSemester());

        String startDate = settingData.getStartDate();
        String endDate = settingData.getEndDate();
        String startTime = settingData.getStartTime();
        String endTime = settingData.getEndTime();
        List<String> roomNumbers =  settingData.getRooms();
        String program = settingData.getProgram();
        String semesterYear = settingData.getSemester();

        List<ProjectWithInstructorsDTO> projectWithInstructorsDTOList = proposalScheduleService.prepareProject(program, semesterYear);

        if(projectWithInstructorsDTOList.isEmpty()) {
            return ResponseEntity.badRequest().body(new ScheduleProposalResponseDTO("error", "There are no project entries in the database"));
        }

        ScheduleProposalResponseDTO scheduleStatus = proposalScheduleService.generateSchedule(startDate, endDate, startTime, endTime, roomNumbers, projectWithInstructorsDTOList);

        if(scheduleStatus.getStatus().equals("error")) {
            return ResponseEntity.badRequest().body(scheduleStatus);
        }

//        String scheduleStatus = proposalScheduleService.generateSchedule(startDate, endDate, startTime, endTime, roomNumbers, projectWithInstructorsDTOList);

        return ResponseEntity.ok(scheduleStatus);
    }

    // อย่าลืมกลับมา detect ค่า null
    @GetMapping("/admin/getAllProposalSchedule")
    @ResponseBody
    public Map<String, Map<Pair<LocalTime, LocalTime>, List<GetProposalScheduleDTO>>> getProposalSchedule(@RequestParam String program, @RequestParam String semesterYear) {
        return manageProposalScheduleService.getProposalSchedule(program, semesterYear);
    }

    // get unSchedule
    @GetMapping("/admin/getAllProposalUnSchedule")
    @ResponseBody
    public List<GetProposalUnScheduleDTO> getProposalUnSchedule(@RequestParam String program, @RequestParam String semesterYear) {
        return manageProposalScheduleService.getProposalUnSchedule(program, semesterYear);
    }

    @GetMapping("/admin/deleteProjectById")
    @ResponseBody
    public Map<String, Boolean> deleteScheduleProject(@RequestParam String projectId) {
        System.out.println("💌 Delete projectId: " + projectId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleteProjectByIdStatus", manageProposalScheduleService.deleteProjectAutoGen(projectId));
        return response;
    }

    @GetMapping("/admin/getDataPreviewProposalSchedule")
    @ResponseBody
    public ResponseEntity<List<PreviewProposalDTO>> getDataPreviewSchedule(@RequestParam String semesterYear) {
        return ResponseEntity.ok(manageProposalScheduleService.getDataPreviewSchedule(semesterYear));
    }



//================================================================= NOT USE =======================================================

//    @GetMapping("/prepareTimeSlot")
//    @ResponseBody
//    public List<ScheduleSlotDTO> prepareTimeSlot(String startDate,String endDate,String startTime,String endTime,List<String> roomNumbers, List<ProjectWithInstructorsDTO> projectWithInstructorsDTOList) {
//        return proposalScheduleService.generateTimeSlot(startDate, endDate, startTime, endTime, roomNumbers, projectWithInstructorsDTOList);
//    }

    // check project
//    @GetMapping("/prepareProject")
//    @ResponseBody
//    public List<ProjectWithInstructorsDTO> prepareProject(String program) {
//        return proposalScheduleService.prepareProject(program);
//    }

}