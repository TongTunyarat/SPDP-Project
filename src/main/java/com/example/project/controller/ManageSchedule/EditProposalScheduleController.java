package com.example.project.controller.ManageSchedule;

import com.example.project.DTO.ManageSchedule.EditSchedule.GetAllEditProposalScheduleDTO;
import com.example.project.service.ManageSchedule.EditProposalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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

    // 👀 เอารายการโปรเจคที่สามารถเเก้ไขได้หลังจากยกเลิก 👀

    @GetMapping("/admin/getProjectEditProposal")
    public ResponseEntity<List<GetAllEditProposalScheduleDTO> > getProjectEditProposal() {
        List<GetAllEditProposalScheduleDTO> response = editProposalService.getProjectEditProposal();
        return ResponseEntity.ok(response);
    }

}
