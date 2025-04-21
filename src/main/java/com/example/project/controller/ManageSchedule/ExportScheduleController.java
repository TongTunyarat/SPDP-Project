package com.example.project.controller.ManageSchedule;

import com.example.project.DTO.ManageSchedule.Preview.PreviewProposalDTO;
import com.example.project.service.ManageSchedule.ExportScheduleService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class ExportScheduleController {

    @Autowired
    ExportScheduleService exportScheduleService;


//    @GetMapping("/admin/exportProposalSchedule")
//    public List<PreviewProposalDTO> exPortProposalSchedule(@RequestParam String program) {
//        return exportScheduleService.getDataExport(program);
//    }

    @GetMapping("/admin/exportProposalSchedule")
    public void exPortProposalSchedule(HttpServletResponse response, @RequestParam String program, @RequestParam String semesterYear) {

        try {
            List<PreviewProposalDTO> data = exportScheduleService.getDataExport(program, semesterYear);
            exportScheduleService.exportToExcel(response, data, program, semesterYear);
        } catch (IOException e) {

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        }
    }

    @GetMapping("/admin/exportDefenseSchedule")
    public void exPortDefenseSchedule(HttpServletResponse response, @RequestParam String semesterYear) {

        try {
            List<PreviewProposalDTO> data = exportScheduleService.getDefenseDataExport(semesterYear);
            exportScheduleService.exportDefenseToExcel(response, data, semesterYear);
        } catch (IOException e) {

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        }
    }

}