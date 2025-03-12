package com.example.project.controller.ProjectManagement;

import com.example.project.DTO.projectManagement.ProfessorRoleDTO;
import com.example.project.DTO.projectManagement.StudentProjectDTO;
import com.example.project.DTO.projectManagement.ProjectDetailsDTO;
import com.example.project.entity.*;
import com.example.project.repository.*;
import com.example.project.service.ProjectManagement.EditProjectService;
import com.example.project.service.ProjectManagement.UploadFilesService;
import com.example.project.service.ProjectService;
import com.example.project.service.ProposalEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    @Autowired
    private EditProjectService editProjectService;
    @Autowired
    private UploadFilesService uploadFilesService;
    @Autowired
    private InstructorRepository instructorRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private StudentProjectRepository studentProjectRepository;

    @Autowired
    public projectAdminController(ProjectService projectService) {
        this.projectService = projectService;
    }


    @GetMapping("/projectOverview")
    @ResponseBody // ส่งข้อมูลเป็น JSON
    public List<ProjectDetailsDTO> getProjectOverview() {
        List<ProjectDetailsDTO> projectDTOList = projectService.getAllProjectsWithProfessor();
        return projectDTOList; // ส่งข้อมูลเป็น JSON
    }

    @GetMapping("/projectOverview/{projectId}")
    public ResponseEntity<ProjectDetailsDTO> getProjectDetails(@PathVariable String projectId) {
        System.out.println("🔍 Fetching project overview for ID: " + projectId);

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

    @GetMapping("/editDetails")
    @ResponseBody
    public ProjectDetailsDTO getEditProjectDetails(@RequestParam String projectId) {
        // ดึงข้อมูลโครงการจาก Service
        Project project = projectService.getProjectDetails(projectId);

        if (project == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
        }

        // ดึงข้อมูลอาจารย์ที่ปรึกษา
        List<ProjectInstructorRole> roles = projectInstructorRoleRepository.findByProjectIdRole_ProjectId(projectId);
        List<ProfessorRoleDTO> professorList = roles.stream()
                .map(role -> new ProfessorRoleDTO(
                        role.getInstructor().getProfessorName(), // ชื่ออาจารย์
                        role.getRole() // บทบาท (Role)
                ))
                .collect(Collectors.toList());

        // ดึงข้อมูลนักศึกษาในโครงการ
        List<StudentProjectDTO> studentList = project.getStudentProjects().stream()
                .filter(studentProject -> "Active".equalsIgnoreCase(studentProject.getStatus()))
                .map(studentProject -> new StudentProjectDTO(
                        studentProject.getStudent().getStudentId(),
                        studentProject.getStudent().getStudentName(),
                        (studentProject.getStudent().getSection() != null)
                                ? studentProject.getStudent().getSection().toString()
                                : "N/A", // ป้องกัน null
                        studentProject.getStudent().getTrack(),
                        studentProject.getStatus()
                ))
                .collect(Collectors.toList());

        // สร้าง DTO เพื่อส่งกลับ
        ProjectDetailsDTO response = new ProjectDetailsDTO(
                project.getProjectId(),
                project.getProjectTitle(),
                professorList,
                project.getProjectDescription(),
                project.getProgram(),
                studentList
        );

        return response;  // Spring จะทำการแปลง ProjectDetailsDTO เป็น JSON
    }

    @PostMapping("/updateProjectDetails")
    public ResponseEntity<Map<String, String>> updateProjectDetails(
            @RequestParam String projectId, @RequestBody ProjectDetailsDTO updatedDetails) {
        try {
            // เรียกใช้ Service เพื่ออัปเดตข้อมูล
            editProjectService.updateProjectDetails(projectId, updatedDetails);

            // ส่งข้อความสำเร็จเป็น JSON
            Map<String, String> response = new HashMap<>();
            response.put("message", "Project details updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // ส่งข้อผิดพลาดเป็น JSON
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/deleteProject/{projectId}")
    public ResponseEntity<String> deleteProject(@PathVariable  String projectId) {
        System.out.println("🔍 Fetching delete project for Project ID: " + projectId);
        try {
            uploadFilesService.deleteProjectDetails(projectId);
            return ResponseEntity.ok("Project deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("Project not found for ID: " + projectId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting project: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteAllProjects")
    public ResponseEntity<String> deleteAllProjects() {
        try {
            uploadFilesService.deleteAllProjects(); // เรียก Service เพื่อลบโปรเจกต์ทั้งหมด
            return ResponseEntity.ok("All projects deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting all projects: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteStudentFromProject")
    public ResponseEntity<Map<String, String>> deleteStudentFromProject(
            @RequestParam String projectId, @RequestParam String studentId) {
        try {
            // เรียกใช้ Service เพื่อลบข้อมูล
            editProjectService.deleteStudentFromProject(projectId, studentId);

            // ส่งข้อความสำเร็จเป็น JSON
            Map<String, String> response = new HashMap<>();
            response.put("message", "Student removed from project successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // ส่งข้อผิดพลาดเป็น JSON
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/addNewProject")
    @ResponseBody
    public ResponseEntity<ProjectDetailsDTO> addProject(@RequestBody ProjectDetailsDTO projectDetailsDTO) {
        // ตรวจสอบว่า projectId มีอยู่ในฐานข้อมูลแล้วหรือไม่
        Project existingProject = projectService.getProjectDetails(projectDetailsDTO.getProjectId());

        if (existingProject != null) {
            // ถ้ามีโปรเจกต์ที่มี projectId ซ้ำ
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Project ID already exists");
        }

        // สร้างโปรเจกต์ใหม่จากข้อมูลที่ส่งมา
        Project newProject = new Project();
        newProject.setProjectId(projectDetailsDTO.getProjectId());
        newProject.setProjectTitle(projectDetailsDTO.getProjectTitle());
        newProject.setProjectDescription(projectDetailsDTO.getProjectDescription());
        newProject.setProgram(projectDetailsDTO.getProgram());

        // บันทึกข้อมูลโปรเจกต์ใหม่
        projectService.saveProject(newProject);

        // ดึงข้อมูลอาจารย์ที่ปรึกษาและบันทึกบทบาทอาจารย์
        List<ProfessorRoleDTO> professorList = projectDetailsDTO.getProfessorList();
        for (ProfessorRoleDTO professorDTO : professorList) {
            ProjectInstructorRole role = new ProjectInstructorRole();

            // ค้นหาข้อมูลอาจารย์ในฐานข้อมูล (ใช้ชื่ออาจารย์)
            Optional<Instructor> instructorOptional = instructorRepository.findByProfessorName(professorDTO.getProfessorName());
            Instructor instructor = instructorOptional.orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Instructor not found: " + professorDTO.getProfessorName())
            );

            // ตั้งค่าอาจารย์ใน ProjectInstructorRole
            role.setProjectIdRole(newProject);  // ตั้งค่าโปรเจกต์
            role.setInstructor(instructor);     // ตั้งค่าอาจารย์
            role.setRole(professorDTO.getRole());  // ตั้งค่าบทบาท
            role.setAssignDate(LocalDateTime.now());  // ตั้งค่าวันที่ที่มอบหมาย
            projectInstructorRoleRepository.save(role);
        }

        // ดึงข้อมูลนักศึกษาและบันทึกข้อมูลนักศึกษา
        List<StudentProjectDTO> studentList = projectDetailsDTO.getStudentList();
        for (StudentProjectDTO studentDTO : studentList) {
            StudentProject studentProject = new StudentProject();

            // ค้นหาข้อมูลนักศึกษาในฐานข้อมูล
            Student student = studentRepository.findByStudentId(studentDTO.getStudentId());
            if (student != null) {
                studentProject.setProject(newProject); // ตั้งค่าโปรเจกต์
                studentProject.setStudent(student);   // ตั้งค่านักศึกษา
                studentProject.setStatus(studentDTO.getStatus()); // ตั้งค่าสถานะ
                studentProject.setStatus("Active");   // ตั้งค่าสถานะเริ่มต้น
                studentProjectRepository.save(studentProject);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found: " + studentDTO.getStudentId());
            }
        }

        // ส่งกลับข้อมูลโปรเจกต์ใหม่ในรูปแบบ DTO
        ProjectDetailsDTO response = new ProjectDetailsDTO(
                newProject.getProjectId(),
                newProject.getProjectTitle(),
                professorList,
                newProject.getProjectDescription(),
                newProject.getProgram(),
                studentList
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);  // ส่งกลับ status CREATED
    }

}



