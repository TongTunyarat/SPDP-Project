package com.example.project.controller.projectManagement;

import com.example.project.DTO.projectManagement.ProfessorRoleDTO;
import com.example.project.DTO.projectManagement.StudentProjectDTO;
import com.example.project.DTO.projectManagement.ProjectDetailsDTO;
import com.example.project.entity.Project;
import com.example.project.entity.ProjectInstructorRole;
import com.example.project.repository.ProjectInstructorRoleRepository;
import com.example.project.repository.ProposalEvaluationRepository;
import com.example.project.service.ProjectService;
import com.example.project.service.ProposalEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class projectAdminController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectInstructorRoleRepository projectInstructorRoleRepository;

    @Autowired
    private ProposalEvaluationService proposalEvaluationService;

    @Autowired
    private ProposalEvaluationRepository proposalEvaluationRepository;

    @GetMapping("/projectOverview")
    @ResponseBody // ส่งข้อมูลเป็น JSON
    public List<ProjectDetailsDTO> getProjectOverview() {
        List<ProjectDetailsDTO> projectDTOList = projectService.getAllProjectsWithProfessor();
        return projectDTOList; // ส่งข้อมูลเป็น JSON
    }

    @GetMapping("/projectOverview/{projectId}")
    public ResponseEntity<ProjectDetailsDTO> getProjectDetails(@PathVariable String projectId) {
        System.out.println("🔍 Fetching project details for ID: " + projectId);

        // ค้นหา Project จากฐานข้อมูล
        Optional<Project> projectOpt = projectService.findById(projectId);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        Project project = projectOpt.get();

        // ค้นหาอาจารย์ที่ปรึกษา
        List<ProjectInstructorRole> roles = projectInstructorRoleRepository.findByProjectIdRole_ProjectId(projectId);
        String professorName = roles.stream()
                .filter(role -> "Advisor".equalsIgnoreCase(role.getRole()))
                .map(role -> role.getInstructor().getProfessorName())
                .findFirst()
                .orElse("Unknown");

        // สร้าง List ของ ProfessorDTO ที่เก็บข้อมูล professorName และ role
        List<ProfessorRoleDTO> professorList = roles.stream()
                .map(role -> new ProfessorRoleDTO(
                        role.getInstructor().getProfessorName(), // ชื่ออาจารย์
                        role.getRole() // บทบาท (Role)
                ))
                .collect(Collectors.toList());

        // ดึงข้อมูลนักศึกษา
        List<StudentProjectDTO> studentList = project.getStudentProjects().stream()
                .filter(studentProject -> "Active".equalsIgnoreCase(studentProject.getStatus()))
                .map(studentProject -> new StudentProjectDTO(
                        studentProject.getStudent().getStudentId(),
                        studentProject.getStudent().getStudentName(),
                        (studentProject.getStudent().getSection() != null)
                                ? studentProject.getStudent().getSection().toString()
                                : "N/A", // ✅ ป้องกัน `null`
                        studentProject.getStudent().getTrack(),
                        studentProject.getStatus()
                ))
                .collect(Collectors.toList());

        // สร้าง DTO เพื่อตอบกลับ
        ProjectDetailsDTO response = new ProjectDetailsDTO(
                project.getProjectId(),
                project.getProjectTitle(),
                professorList,
                project.getProjectDescription(),
                project.getProgram(),
                studentList
        );

        return ResponseEntity.ok(response);
    }
}


