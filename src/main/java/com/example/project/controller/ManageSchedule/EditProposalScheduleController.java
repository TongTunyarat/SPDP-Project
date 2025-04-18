package com.example.project.controller.ManageSchedule;

import com.example.project.DTO.ManageSchedule.BookingSettingsDTO;
import com.example.project.DTO.ManageSchedule.EditSchedule.BookingSettingsProposalDTO;
import com.example.project.DTO.ManageSchedule.EditSchedule.GetAllEditProposalScheduleDTO;
import com.example.project.DTO.ManageSchedule.EditSchedule.GetEditProposalScheduleByIdDTO;
import com.example.project.DTO.ManageSchedule.EditSchedule.ProposalConflictDTO;
import com.example.project.DTO.ManageSchedule.ProjectWithInstructorsDTO;
import com.example.project.DTO.ManageSchedule.ScheduleProposalResponseDTO;
import com.example.project.entity.ProposalSchedule;
import com.example.project.service.ManageSchedule.EditProposalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.swing.text.html.parser.Entity;
import java.util.List;

@Controller
public class EditProposalScheduleController {

    @Autowired
    EditProposalService editProposalService;

    @GetMapping("/admin/editProposalSchedulePage")
    public String getEditProposalPage() {

        return "ManageSchedule/EditProposalTiming";
    }

    @GetMapping("/admin/previewProposalSchedulePage")
    public String getPreviewProposalPage() {

        return "ManageSchedule/PreviewProposalSchedule";
    }

    // 👀 เอารายการโปรเจคที่สามารถเเก้ไขได้หลังจากยกเลิก 👀
    @GetMapping("/admin/getProjectEditProposal")
    public ResponseEntity<List<GetAllEditProposalScheduleDTO> > getProjectEditProposal(@RequestParam String semesterYear) {
        List<GetAllEditProposalScheduleDTO> response = editProposalService.getProjectEditProposal(semesterYear);
        return ResponseEntity.ok(response);
    }

    // ดึงค่าสำหรับใน cardpopup
    @GetMapping("/admin/getProjectEditProposalByProjectId")
    public ResponseEntity<List<GetEditProposalScheduleByIdDTO> > getProjectEditProposalById(@RequestParam String projectId) {
        List<GetEditProposalScheduleByIdDTO> response = editProposalService.getProjectEditProposalByProjectId(projectId);
        return ResponseEntity.ok(response);
    }

    // เเก้ไขตารางการนำเสนอ
    @PostMapping("/admin/EditProposalByProjectId")
    public ResponseEntity<ProposalConflictDTO> editProposalProjectId(@RequestBody BookingSettingsProposalDTO settingData) {
        System.out.println("💌 Received Booking Data:");
        System.out.println("ProjectId: " + settingData.getProjectId());
        System.out.println("Program: " + settingData.getProgram());
        System.out.println("Start Date: " + settingData.getStartDate());
        System.out.println("Start Time: " + settingData.getStartTime());
        System.out.println("End Time: " + settingData.getEndTime());
        System.out.println("Room: " + settingData.getRoom());

        String projectId = settingData.getProjectId();
        String program = settingData.getStartDate();
        String startDate = settingData.getStartDate();
        String startTime = settingData.getStartTime();
        String endTime = settingData.getEndTime();
        String roomNumber =  settingData.getRoom();

        ProposalConflictDTO response = editProposalService.editProposalByprojectId(projectId, program, startDate, startTime, endTime, roomNumber);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/see")
    public ResponseEntity<String> saveBookingSettings(@RequestBody BookingSettingsProposalDTO settingData) {
        System.out.println("💌 Received Booking Data:");
        System.out.println("ProjectId: " + settingData.getProjectId());
        System.out.println("Program: " + settingData.getProgram());
        System.out.println("Start Date: " + settingData.getStartDate());
        System.out.println("Start Time: " + settingData.getStartTime());
        System.out.println("End Time: " + settingData.getEndTime());
        System.out.println("Room: " + settingData.getRoom());

        return ResponseEntity.ok("OK");
    }


}