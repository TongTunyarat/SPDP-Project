package com.example.project.controller;

import com.example.project.entity.Project;
import com.example.project.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.project.DTO.InstructorProjectDTO;
import com.example.project.DTO.StudentProjectDTO;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
public class DefenseEvaluationController {

    @Autowired
    private ProjectService projectService;

    // send project when click edit
    @GetMapping("/instructor/editDefenseEvaluation")
    public String getProjectDetails(@RequestParam String projectId){
        return "GiveScore/GiveDefEvaScore";
    }

    // project list by user with specific projectId
    @GetMapping("/instructor/giveScoreDefenseEvaluation")
    @ResponseBody
    public InstructorProjectDTO getProjectById(@RequestParam String projectId) {
        // ดึงข้อมูลโครงการด้วย service ที่มีอยู่
        Project project = projectService.getProjectDetails(projectId);

        if (project == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
        }

        // แปลงข้อมูลนักศึกษาในโครงการ
        List<StudentProjectDTO> studentProjectDTOS = ((Project) project).getStudentProjects().stream()
                .map(studentProject -> new StudentProjectDTO(
                        studentProject.getStudent().getStudentId(),
                        studentProject.getStudent().getStudentName()))
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
