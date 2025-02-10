package com.example.project.controller;

import com.example.project.DTO.InstructorProjectDTO;
import com.example.project.DTO.StudentProjectDTO;
import com.example.project.entity.Project;
import com.example.project.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
public class GradeScoreController {

    @Autowired
    private ProjectService projectService;

    //-------------------- Proposal Evaluation --------------------//

    // send project when click edit
    @GetMapping("/instructor/editProposalGrade")
    public String getProjectPropDetails(@RequestParam String projectId, Model model){

        model.addAttribute("projectId", projectId);

        return "GiveScore/GivePropGradeScore";
    }

    // project list by user with specific projectId
    @GetMapping("/instructor/giveScoreProposalGrade")
    @ResponseBody
    public InstructorProjectDTO getProjectPropById(@RequestParam String projectId) {
        // ดึงข้อมูลโครงการด้วย service ที่มีอยู่
        Project project = projectService.getProjectDetails(projectId);

        if (project == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
        }

        // แปลงข้อมูลนักศึกษาในโครงการ
        List<StudentProjectDTO> studentProjectDTOS = ((Project) project).getStudentProjects().stream()
                .map(studentProject -> new StudentProjectDTO(
                        studentProject.getStudent().getStudentId(),
                        studentProject.getStudent().getStudentName(),
                        studentProject.getStatus()))
                .toList();

        // Return DTO ตามข้อมูลโครงการที่ดึงมา
        return new InstructorProjectDTO(
                project.getProgram(),
                project.getProjectId(),
                project.getProjectTitle(),
                "Your Role", // ใส่ข้อมูล role ที่เหมาะสมหรือดึงจากที่อื่นหากมี
                studentProjectDTOS
        );
    }

    // -------------------- Defense Evaluation -------------------- //

    // send project when click edit
    @GetMapping("/instructor/editDefenseGrade")
    public String getProjectDefDetails(@RequestParam String projectId, Model model){
        model.addAttribute("projectId", projectId);
        return "GiveScore/GiveDefGradeScore";
    }

    // project list by user with specific projectId
    @GetMapping("/instructor/giveScoreDefenseGrade")
    @ResponseBody
    public InstructorProjectDTO getProjectDefById(@RequestParam String projectId) {
        // ดึงข้อมูลโครงการด้วย service ที่มีอยู่
        Project project = projectService.getProjectDetails(projectId);

        if (project == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
        }

        // แปลงข้อมูลนักศึกษาในโครงการ
        List<StudentProjectDTO> studentProjectDTOS = ((Project) project).getStudentProjects().stream()
                .map(studentProject -> new StudentProjectDTO(
                        studentProject.getStudent().getStudentId(),
                        studentProject.getStudent().getStudentName(),
                        studentProject.getStatus()))
                .toList();

        // Return DTO ตามข้อมูลโครงการที่ดึงมา
        return new InstructorProjectDTO(
                project.getProgram(),
                project.getProjectId(),
                project.getProjectTitle(),
                "Your Role", // ใส่ข้อมูล role ที่เหมาะสมหรือดึงจากที่อื่นหากมี
                studentProjectDTOS
        );
    }
}
