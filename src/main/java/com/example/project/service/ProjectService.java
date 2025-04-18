package com.example.project.service;


import com.example.project.DTO.projectManagement.ProfessorRoleDTO;
import com.example.project.DTO.projectManagement.ProjectDTO;
import com.example.project.DTO.projectManagement.ProjectDetailsDTO;
import com.example.project.DTO.projectManagement.StudentProjectDTO;
import com.example.project.entity.*;
import com.example.project.repository.AccountRepository;
import com.example.project.repository.ProjectInstructorRoleRepository;
import com.example.project.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.SpringVersion;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProjectInstructorRoleRepository projectInstructorRoleRepository;

    public ProjectService(AccountRepository accountRepository, ProjectRepository projectRepository, ProjectInstructorRoleRepository projectInstructorRoleRepository) {
        this.accountRepository = accountRepository;
        this.projectRepository = projectRepository;
        this.projectInstructorRoleRepository = projectInstructorRoleRepository;
    }

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }


    //=========================================== USE ===================================================

    // find project by instructor user - show project page
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


    //    get project details after click edit
    public Project getProjectDetails(String projectId) {
        return projectRepository.findByProjectId(projectId);

    }
    public List<StudentProject> getStudentDetails(String projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));
        return project.getStudentProjects();
    }

    public List<ProjectDetailsDTO> getAllProjectsWithProfessor() {
        List<Project> projects = projectRepository.findAll(); // Get all projects from database
        List<ProjectDetailsDTO> projectDetailsDTOList = new ArrayList<>();

        for (Project project : projects) {
            // ดึงข้อมูลจาก ProjectInstructorRole สำหรับ project นี้
            List<ProjectInstructorRole> roles = projectInstructorRoleRepository.findByProjectIdRole_ProjectId(project.getProjectId());

            // สร้าง professorList ที่เก็บชื่ออาจารย์พร้อมกับ role
            List<ProfessorRoleDTO> professorList = new ArrayList<>();
            for (ProjectInstructorRole roleData : roles) {
                professorList.add(new ProfessorRoleDTO(roleData.getInstructor().getProfessorName(), roleData.getRole(), roleData.getInstructorId()));
            }

            // ตรวจสอบ program ถ้าเป็น null ให้ตั้งค่าเป็น "Unknown"
            String program = project.getProgram() != null ? project.getProgram() : "Unknown";

            // ดึงข้อมูลนักศึกษา (เฉพาะนักศึกษาที่ Active)
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

            // สร้าง ProjectDetailsDTO และใส่ข้อมูลลงไป
            ProjectDetailsDTO projectDetailsDTO = new ProjectDetailsDTO(
                    project.getProjectId(),
                    project.getProjectTitle(),
                    professorList,
                    project.getProjectDescription(),
                    program,  // ส่งโปรแกรม
                    studentList, // ส่งข้อมูลนักศึกษา
                    project.getProjectCategory(),
                    project.getSemester()
            );

            projectDetailsDTOList.add(projectDetailsDTO);
        }

        return projectDetailsDTOList;
    }

    public Optional<Project> findById(String projectId) {
        return projectRepository.findById(projectId);
    }

    // บันทึกโปรเจกต์ใหม่
    public void saveProject(Project project) {
        projectRepository.save(project);
    }











































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