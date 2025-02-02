package com.example.project.controller;

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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;

@RestController
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }


    @GetMapping("/instructor/editEvaluation")
    @ResponseBody
    public ResponseEntity<?> getProjectDetails(@RequestParam String projectId, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Account username: " + authentication.getName());
        System.out.println("Session ID: " + RequestContextHolder.currentRequestAttributes().getSessionId());

        Project projectDetails = projectService.getProjectDetails(projectId);
        if (projectDetails == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Project Details");
        }
        return ResponseEntity.ok(projectDetails);
    }






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