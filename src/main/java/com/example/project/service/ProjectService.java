package com.example.project.service;


import com.example.project.entity.*;
import com.example.project.repository.AccountRepository;
import com.example.project.repository.ProjectRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final AccountRepository accountRepository;
    private final ProjectRepository projectRepository;

    public ProjectService(AccountRepository accountRepository, ProjectRepository projectRepository) {
        this.accountRepository = accountRepository;
        this.projectRepository = projectRepository;
    }

    //=========================================== USE ===================================================

    // find project by instructor user
    public List<ProjectInstructorRole> getInstructorProject() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // มี user login อยู่ไหม
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();

            Account account = accountRepository.findAccountByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User account not found"));

            // instructor ใน account
            Instructor instructor = account.getInstructors();
            // projectInstructorRole ใน instructor เอาข้อมูลทุกอย่าง
            List<ProjectInstructorRole> projectInstructorRole = instructor.getProjectInstructorRoles();
            return projectInstructorRole;
        }
        throw new UsernameNotFoundException("User is not authenticated");
    }

//     get project details
    public Project getProjectDetails(String projectId) {

        return projectRepository.findByProjectId(projectId);

    }
//
//    public static List<StudentProject> getProjectById(String projectId) {
//
//        Project project = projectRepository.findByProjectId(projectId);
//
//        List<StudentProject> studentProjects = project.getStudentProjects();
//
//        return studentProjects;
//
//    }


//
//    public Project getProjectDetails(String projectId) {
//        Object[] result = projectRepository.findProfessorNameByProjectId(projectId);
//        Project project = (Project) result[0];
//        String professorName = (String) result[1];
//        project.setProfessorName(professorName);  // ตั้งค่า professorName ให้กับ Project
//        return project;
//    }








































//=========================================== See Result (Not Use) ===================================================

    // ดึงข้อมูลผู้ใช้เมื่อ login เข้าระบบ
    public Instructor getInstructorUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // มี user login อยู่บ้างไหม
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();

            Account account = accountRepository.findAccountByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User account not found"));

            // instructor ใน account
            Instructor instructor = account.getInstructors();
            return instructor;
        }
        throw new UsernameNotFoundException("User is not authenticated");
    }

    public Admin getAdminUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // มี user login อยู่บ้างไหม
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();

            Account account = accountRepository.findAccountByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User account not found"));

            // admin ใน account
            Admin admin = account.getAdmins();
            return admin;
        }
        throw new UsernameNotFoundException("User is not authenticated");
    }

}