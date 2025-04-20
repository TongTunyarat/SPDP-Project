package com.example.project.controller.ManageSchedule.DefenseSchedule;

import com.example.project.DTO.ManageSchedule.EditSchedule.BookingSettingsProposalDTO;
import com.example.project.DTO.ManageSchedule.EditSchedule.GetAllEditProposalScheduleDTO;
import com.example.project.DTO.ManageSchedule.EditSchedule.GetEditProposalScheduleByIdDTO;
import com.example.project.DTO.ManageSchedule.EditSchedule.ProposalConflictDTO;
import com.example.project.DTO.ManageSchedule.Preview.PreviewProposalDTO;
import com.example.project.service.ManageSchedule.DefenseSchedule.EditDefenseService;
import com.example.project.service.ManageSchedule.DefenseSchedule.ManageDefenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class EditDefenseController {
    @Autowired
    EditDefenseService editDefenseService;
    @Autowired
    ManageDefenseService manageDefenseService;

    @GetMapping("/admin/editDefenseSchedulePage")
    public String getEditDefensePage() {

        return "ManageSchedule/EditDefenseTiming";
    }

    @GetMapping("/admin/previewDefenseSchedulePage")
    public String getPreviewDefensePage() {

        return "ManageSchedule/PreviewDefenseSchedule";
    }

    // 👀 เอารายการโปรเจคที่สามารถเเก้ไขได้หลังจากยกเลิก 👀
    @GetMapping("/admin/getProjectEditDefense")
    public ResponseEntity<List<GetAllEditProposalScheduleDTO>> getProjectEditDefense(@RequestParam String semesterYear) {
        List<GetAllEditProposalScheduleDTO> response = editDefenseService.getProjectEditDefense(semesterYear);
        return ResponseEntity.ok(response);
    }

    // ดึงค่าสำหรับใน cardpopup
    @GetMapping("/admin/getProjectEditDefenseByProjectId")
    public ResponseEntity<List<GetEditProposalScheduleByIdDTO> > getProjectEditDefenseById(@RequestParam String projectId) {
        List<GetEditProposalScheduleByIdDTO> response = editDefenseService.getProjectEditDefenseByProjectId(projectId);
        return ResponseEntity.ok(response);
    }

    // เเก้ไขตารางการนำเสนอ
    @PostMapping("/admin/EditDefenseByProjectId")
    public ResponseEntity<ProposalConflictDTO> editDefenseProjectId(@RequestBody BookingSettingsProposalDTO settingData) {
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

        ProposalConflictDTO response = editDefenseService.editDefenseByprojectId(projectId, program, startDate, startTime, endTime, roomNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/getDataPreviewDefenseSchedule")
    @ResponseBody
    public ResponseEntity<List<PreviewProposalDTO>> getDataPreviewSchedule(@RequestParam String semesterYear) {
        return ResponseEntity.ok(manageDefenseService.getDataDefensePreviewSchedule(semesterYear));
    }
}