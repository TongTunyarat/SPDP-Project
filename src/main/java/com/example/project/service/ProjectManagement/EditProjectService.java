package com.example.project.service.ProjectManagement;

import com.example.project.DTO.projectManagement.ProjectDetailsDTO;
import com.example.project.entity.Project;
import com.example.project.entity.ProjectInstructorRole;
import com.example.project.entity.StudentProject;
import com.example.project.repository.ProjectInstructorRoleRepository;
import com.example.project.repository.ProjectRepository;
import com.example.project.repository.StudentProjectRepository;
import com.example.project.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EditProjectService {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProjectInstructorRoleRepository projectInstructorRoleRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private StudentProjectRepository studentProjectRepository;

    @Transactional
    public void updateProjectDetails(String projectId, ProjectDetailsDTO updatedDetails) {
        // ดึงข้อมูลโปรเจกต์จากฐานข้อมูล
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found for ID: " + projectId));

        // ตรวจสอบว่าโปรเจกต์มีการแก้ไขข้อมูลหรือไม่
        boolean isUpdated = false;

        // ตรวจสอบการแก้ไขข้อมูลโปรเจกต์
        if (!project.getProjectTitle().equals(updatedDetails.getProjectTitle())) {
            project.setProjectTitle(updatedDetails.getProjectTitle());
            isUpdated = true;
        }
        if (!project.getProjectDescription().equals(updatedDetails.getProjectDescription())) {
            project.setProjectDescription(updatedDetails.getProjectDescription());
            isUpdated = true;
        }
        if (!project.getProgram().equals(updatedDetails.getProgram())) {
            project.setProgram(updatedDetails.getProgram());
            isUpdated = true;
        }

        // อัปเดตข้อมูลอาจารย์ที่ปรึกษา
        List<ProjectInstructorRole> existingRoles = projectInstructorRoleRepository.findByProjectIdRole_ProjectId(projectId);

        // เช็คว่า professorList ไม่เป็น null ก่อนใช้งาน
        List<ProjectInstructorRole> updatedRoles = Optional.ofNullable(updatedDetails.getProfessorList())
                .orElse(Collections.emptyList())  // ถ้า professorList เป็น null ให้ใช้ List ว่าง
                .stream()
                .map(professor -> {
                    // ค้นหาหรือสร้าง ProjectInstructorRole ใหม่ตามข้อมูลที่ได้
                    ProjectInstructorRole role = existingRoles.stream()
                            .filter(r -> r.getInstructor().getProfessorName().equals(professor.getProfessorName()))
                            .findFirst()
                            .orElse(new ProjectInstructorRole(project));  // ถ้าไม่เจอให้สร้างใหม่
                    role.setRole(professor.getRole());
                    return role;
                })
                .collect(Collectors.toList());

        // ตรวจสอบและบันทึกการเปลี่ยนแปลงอาจารย์ที่ปรึกษา
        projectInstructorRoleRepository.saveAll(updatedRoles);

        // อัปเดตข้อมูลนักศึกษา
        List<StudentProject> updatedStudents = Optional.ofNullable(updatedDetails.getStudentList())
                .orElse(Collections.emptyList())  // ถ้า studentList เป็น null ให้ใช้ List ว่าง
                .stream()
                .map(student -> {
                    // หาคู่ที่ตรงกันในฐานข้อมูล
                    StudentProject studentProject = project.getStudentProjects().stream()
                            .filter(sp -> sp.getStudent().getStudentId().equals(student.getStudentId()))
                            .findFirst()
                            .orElse(new StudentProject(project, student.getStudentId()));  // ถ้าไม่เจอให้สร้างใหม่

                    // กำหนดค่า status ใหม่
                    studentProject.setStatus(student.getStatus() != null ? student.getStatus() : "Active");

                    return studentProject;
                })
                .collect(Collectors.toList());

        // ตรวจสอบและบันทึกการเปลี่ยนแปลงนักศึกษา
        studentProjectRepository.saveAll(updatedStudents);

        // ถ้ามีการแก้ไขใดๆ ให้บันทึกโปรเจกต์
        if (isUpdated) {
            projectRepository.save(project);
        }
    }

    @Transactional
    public void deleteStudentFromProject(String projectId, String studentId) {
        // ค้นหาข้อมูล studentProject ที่ตรงกัน
        StudentProject studentProject = studentProjectRepository.findByProject_ProjectIdAndStudentStudentId(projectId, studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found for this project"));

        // ลบ student จาก project
        studentProjectRepository.delete(studentProject);
    }


}
