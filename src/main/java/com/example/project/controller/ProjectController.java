package com.example.project.controller;

import com.example.project.DTO.InstructorProjectDTO;
import com.example.project.DTO.StudentProjectDTO;
import com.example.project.entity.Admin;
import com.example.project.entity.Instructor;
import com.example.project.entity.Project;
import com.example.project.entity.ProjectInstructorRole;
import com.example.project.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProjectController {

    @Autowired
    private final ProjectService projectService;


    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    //=========================================== USE ===================================================

    // show project list page
    @GetMapping("/instructor/view")
    public String viewInstructorProjectPage() {
        System.out.println("Show project proposal page");
        return "ShowProposalEvaProject"; // html
    }

    // project list by user
    @GetMapping("/instructor/projectList")
    @ResponseBody
    public List<InstructorProjectDTO> getInstructorData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Account username: " + authentication.getName());
        System.out.println("Session ID: " + RequestContextHolder.currentRequestAttributes().getSessionId());

        List<ProjectInstructorRole> projectInstructorRoles = projectService.getInstructorProject();

        System.out.println("Find project list");

        // return JSON
        return projectInstructorRoles.stream()
                .map(i -> {
                    // getStudentProjects -> studentProjects (Project Entity) => (StudentProjects Entity)
                    List<StudentProjectDTO> studentProjectDTOS = i.getProjectIdRole().getStudentProjects().stream()
                            .map(studentProject -> new StudentProjectDTO(
                                    // getStudent() -> ใน (StudentProjects Entity)
                                    studentProject.getStudent().getStudentId(),
                                    studentProject.getStudent().getStudentName()))
                            .toList();

                    return new InstructorProjectDTO(
                            // i -> projectInstructorRoles
                            // i.getProjectIdRole() -> Project (ProjectInstructorRole Entity)
                            // getProjectId() -> Id (Project Entity)
                            i.getProjectIdRole().getProgram(),
                            i.getProjectIdRole().getProjectId(),
                            i.getProjectIdRole().getProjectTitle(),
                            i.getRole(),
                            studentProjectDTOS
                    );
                }).collect(Collectors.toList());
    }














    //=========================================== NOT USE ===================================================


//    // send project when click edit
//    @GetMapping("/instructor/editProposalEvaluation")
//    @ResponseBody
//    public ResponseEntity<?> getProjectDetails(@RequestParam String projectId){
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        System.out.println("Account username: " + authentication.getName());
//        System.out.println("Session ID: " + RequestContextHolder.currentRequestAttributes().getSessionId());
//
//        Project projectDetails = projectService.getProjectDetails(projectId);
//        if (projectDetails == null) {
//            System.out.println("Umm");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Project Details");
//        }
//        System.out.println("Project ID: " + projectId);
//        System.out.println("Project Detail: " + projectDetails);
//        return ResponseEntity.ok(projectDetails);
//    }
































    //=========================================== See Result (Not Use) ===================================================
    // ข้อมูล instructor project
    @GetMapping("/instructor/project")
    public ResponseEntity<?> getInstructorProject() {
        try {
            List<ProjectInstructorRole> projectInstructorRoles = projectService.getInstructorProject();
            if (projectInstructorRoles.isEmpty()){
                return ResponseEntity.ok("No project data available.");
            }
            return ResponseEntity.ok(projectInstructorRoles);
        } catch (UsernameNotFoundException ex) {
            // ไม่พบข้อมูล หรือ ผู้ใช้ไม่ได้ login
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);  // 401 Unauthorized
        }
    }

    // ดึงข้อมูลผู้ใช้เมื่อ login เข้าระบบ
    @GetMapping("/instructor/details")
    public ResponseEntity<Instructor> getInstructorUserDetails() {
        try {
            Instructor instructor = projectService.getInstructorUserDetails();
            return ResponseEntity.ok(instructor);
        } catch (UsernameNotFoundException ex) {
            // ไม่พบข้อมูล หรือ ผู้ใช้ไม่ได้ login
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);  // 401 Unauthorized
        }
    }

    @GetMapping("/admin/details")
    @ResponseBody
    public ResponseEntity<Admin> getAdminUserDetails() {
        try {
            Admin admin = projectService.getAdminUserDetails();
            return ResponseEntity.ok(admin);
        } catch (UsernameNotFoundException ex) {
            // ไม่พบข้อมูล หรือ ผู้ใช้ไม่ได้ login
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);  // 401 Unauthorized
        }
    }
}