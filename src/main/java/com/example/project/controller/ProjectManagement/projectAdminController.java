package com.example.project.controller.ProjectManagement;

import com.example.project.DTO.projectManagement.*;
import com.example.project.entity.*;
import com.example.project.repository.*;
import com.example.project.service.ProjectManagement.EditProjectService;
import com.example.project.service.ProjectManagement.UploadFilesService;
import com.example.project.service.ProjectManagement.AddNewProjectService;
import com.example.project.service.ProjectService;
import com.example.project.service.ProposalEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
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
    private AddNewProjectService AddNewProjectService;
    @Autowired
    private EditProjectService EditProjectService;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProjectInstructorRoleRepository ProjectInstructorRoleRepository;
    @Autowired
    private AddNewProjectService addNewProjectService;

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
                        role.getRole(), // บทบาท (Role)
                        role.getInstructorId()
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
                studentList,
                project.getProjectCategory(),
                project.getSemester()
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
                        role.getRole(), // บทบาท (Role)
                        role.getInstructorId()
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
                studentList,
                project.getProjectCategory(),
                project.getSemester()
        );

        return response;  // Spring จะทำการแปลง ProjectDetailsDTO เป็น JSON
    }

    // ============= UPDATE PROJECT ============= //

    @PostMapping("/updateProjectDetails")
    public ResponseEntity<?> updateProject(
            @RequestParam String projectId,
            @RequestBody ProjectDetailsDTO updatedDetails
    ) {
        List<String> errors = EditProjectService.updateProjectDetails(projectId, updatedDetails);
        if (!errors.isEmpty()) {
            // คืน 400 พร้อม list ของ messages
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Validation failed", "errors", errors));
        }
        return ResponseEntity.ok(Map.of("message", "Project updated successfully"));
    }



    // ============= DELETE PROJECT ============= //

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


//    @DeleteMapping("/deleteProjectsBySemester")
//    public ResponseEntity<String> deleteBySemester(@RequestParam String semester) {
//        uploadFilesService.deleteProjectsBySemester(semester);
//        return ResponseEntity.ok("Deleted projects for semester " + semester);
//    }

    @DeleteMapping("/deleteProjectsBySemester")
    public ResponseEntity<String> deleteBySemester(
            @RequestParam String semester,
            @RequestParam(defaultValue = "all") String program) {

        uploadFilesService.deleteProjectsBySemesterAndProgram(semester, program);

        String msg = "Deleted " +
                ("all".equalsIgnoreCase(program) ? "all projects" : program.toUpperCase() + " projects") +
                " for semester " + semester;
        return ResponseEntity.ok(msg);
    }


    @Transactional
    @DeleteMapping("/deleteStudentFromProject")
    public ResponseEntity<String> deleteStudentFromProject(
            @RequestParam String projectId,
            @RequestParam String studentId) {
        // ยืนยันว่ามี record จริง
        boolean exists = studentProjectRepository
                .existsByProject_ProjectIdAndStudent_StudentId(projectId, studentId);
        if (!exists) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error: Student not found for this project");
        }
        // ลบ
        studentProjectRepository
                .deleteByProject_ProjectIdAndStudent_StudentId(projectId, studentId);
        return ResponseEntity.ok("Student removed successfully");
    }

    @Transactional
    @DeleteMapping("/deleteInstructorFromProject")
    public ResponseEntity<String> deleteInstructorFromProject(
            @RequestParam String projectId,
            @RequestParam String professorId) {

        Project project = projectRepository.findByProjectId(projectId);
        if (project == null) {
            return ResponseEntity.badRequest().body("Project not found");
        }
        Instructor instructor = instructorRepository.findByProfessorId(professorId);
        if (instructor == null) {
            return ResponseEntity.badRequest().body("Instructor not found");
        }

        // เรียกเมธอดใหม่ที่พึ่งเพิ่ม
        projectInstructorRoleRepository
                .deleteByProjectIdRole_ProjectIdAndInstructor_ProfessorId(projectId, professorId);

        return ResponseEntity.ok("Instructor removed successfully");
    }



    // ============= ADD NEW PROJECT ============= //

    // รับ JSON จาก JS แล้วสร้าง Project ใหม่
    @PostMapping("/addProject")
    public ResponseEntity<?> addProject(@RequestBody NewProjectDTO dto) {
        try {
            String projectId = addNewProjectService.createProject(dto);
            return ResponseEntity.ok(Map.of("projectId", projectId));
        } catch (IllegalArgumentException e) {
            // แยกข้อความกลับเป็น list
            List<String> warnings = Arrays.stream(e.getMessage().split(";"))
                    .map(String::trim).toList();
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("warnings", warnings));
        }
    }

    @GetMapping("/getLastProjectId")
    public ResponseEntity<String> getLastProjectId(
            @RequestParam String program,
            @RequestParam String semester
    ) {
        return ResponseEntity.ok(
                AddNewProjectService.findLatestProjectId(program, semester)
        );
    }

    @GetMapping("/getStudentsWithoutProject")
    public ResponseEntity<List<Student>> getStudentsWithoutProject() {
        List<Student> students = AddNewProjectService.getStudentsWithoutProject();  // ค้นหานักศึกษาที่ยังไม่มีกระบวนการโปรเจกต์
        return ResponseEntity.ok(students);
    }

    @GetMapping("/getInstructorsWithoutProject")
    public ResponseEntity<List<Instructor>> getInstructorsWithoutProject() {
        List<Instructor> instructors = AddNewProjectService.getInstructorsWithoutProject();
        return ResponseEntity.ok(instructors);
    }

    @GetMapping("/getInstructors")
    public ResponseEntity<List<Instructor>> getInstructors() {
        List<Instructor> instructors = instructorRepository.findAll();  // ดึงข้อมูลอาจารย์ทั้งหมดจากฐานข้อมูล
        return ResponseEntity.ok(instructors);  // ส่งข้อมูลอาจารย์ไปยังฝั่ง Client
    }



    /** คืน `true` ถ้ามี Advisor สำหรับ projectId นั้นอยู่แล้ว */
    @GetMapping("/hasAdvisor")
    public ResponseEntity<Boolean> hasAdvisorBySemester(@RequestParam("semester") String semester) {
        boolean has = projectInstructorRoleRepository
                .existsByProjectIdRole_SemesterAndRole(semester, "Advisor");
        return ResponseEntity.ok(has);
    }

    @GetMapping("/hasCommittee")
    public ResponseEntity<Boolean> hasCommittee(@RequestParam("semester") String semester) {
        boolean has = projectInstructorRoleRepository
                .existsByProjectIdRole_SemesterAndRole(semester, "Committee");
        return ResponseEntity.ok(has);
    }

    @GetMapping("/hasPosterCommittee")
    public ResponseEntity<Boolean> hasPosterCommittee(@RequestParam("semester") String semester) {
        boolean has = projectInstructorRoleRepository
                .existsByProjectIdRole_SemesterAndRole(semester, "Poster-Committee");
        return ResponseEntity.ok(has);
    }

    @GetMapping("/hasAdvisorProgram")
    public ResponseEntity<Boolean> hasAdvisorProgram(
            @RequestParam String semester,
            @RequestParam String program) {

        boolean exists;
        if ("all".equalsIgnoreCase(program)) {
            exists = projectInstructorRoleRepository
                    .existsByProjectIdRoleSemesterAndRole(semester, "Advisor");
        } else {
            exists = projectInstructorRoleRepository
                    .existsByProjectIdRoleSemesterAndRoleAndProjectIdRoleProgram(
                            semester, "Advisor", program);
        }
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/hasCommitteeProgram")
    public ResponseEntity<Boolean> hasCommitteeProgram(
            @RequestParam String semester,
            @RequestParam String program) {

        boolean exists;
        if ("all".equalsIgnoreCase(program)) {
            exists = projectInstructorRoleRepository
                    .existsByProjectIdRoleSemesterAndRole(semester, "Committee");
        } else {
            exists = projectInstructorRoleRepository
                    .existsByProjectIdRoleSemesterAndRoleAndProjectIdRoleProgram(
                            semester, "Committee", program);
        }
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/hasPosterCommitteeProgram")
    public ResponseEntity<Boolean> hasPosterCommitteeProgram(
            @RequestParam String semester,
            @RequestParam String program) {

        boolean exists;
        if ("all".equalsIgnoreCase(program)) {
            exists = projectInstructorRoleRepository
                    .existsByProjectIdRoleSemesterAndRole(semester, "Poster-Committee");
        } else {
            exists = projectInstructorRoleRepository
                    .existsByProjectIdRoleSemesterAndRoleAndProjectIdRoleProgram(
                            semester, "Poster-Committee", program);
        }
        return ResponseEntity.ok(exists);
    }


//    @GetMapping("/hasAdvisor")
//    public ResponseEntity<Boolean> hasAdvisor(@RequestParam String semester) {
//        boolean has = projectInstructorRoleRepository
//                .existsByProjectIdRole_SemesterAndRole(semester, "Advisor");
//        return ResponseEntity.ok(has);
//    }
//
//    @GetMapping("/hasCommittee")
//    public ResponseEntity<Boolean> hasCommittee(@RequestParam String semester) {
//        boolean has = projectInstructorRoleRepository
//                .existsByProjectIdRole_SemesterAndRole(semester, "Committee");
//        return ResponseEntity.ok(has);
//    }
//
//    @GetMapping("/hasPosterCommittee")
//    public ResponseEntity<Boolean> hasPosterCommittee(@RequestParam String semester) {
//        boolean has = projectInstructorRoleRepository
//                .existsByProjectIdRole_SemesterAndRole(semester, "Poster-Committee");
//        return ResponseEntity.ok(has);
//    }



}



