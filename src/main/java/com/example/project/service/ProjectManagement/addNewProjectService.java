package com.example.project.service.ProjectManagement;

import com.example.project.DTO.projectManagement.ProfessorRoleDTO;
import com.example.project.DTO.projectManagement.ProjectDetailsDTO;
import com.example.project.DTO.projectManagement.StudentProjectDTO;
import com.example.project.entity.*;
import com.example.project.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class addNewProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectInstructorRoleRepository projectInstructorRoleRepository;

    @Autowired
    private StudentProjectRepository studentProjectRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Transactional
    public ProjectDetailsDTO addNewProject(ProjectDetailsDTO projectDetailsDTO) {
        // ตรวจสอบว่า projectId มีอยู่ในฐานข้อมูลแล้วหรือไม่
        Project existingProject = projectRepository.findById(projectDetailsDTO.getProjectId())
                .orElse(null);

        if (existingProject != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Project ID already exists");
        }

        // สร้างโปรเจกต์ใหม่
        Project newProject = new Project();
        newProject.setProjectId(projectDetailsDTO.getProjectId());
        newProject.setProjectTitle(projectDetailsDTO.getProjectTitle());
        newProject.setProjectDescription(projectDetailsDTO.getProjectDescription());
        newProject.setProgram(projectDetailsDTO.getProgram());

        // บันทึกโปรเจกต์ใหม่
        projectRepository.save(newProject);

        // เพิ่มข้อมูลอาจารย์ที่ปรึกษา
        List<ProfessorRoleDTO> professorList = projectDetailsDTO.getProfessorList();
        if (professorList != null) {
            for (ProfessorRoleDTO professorDTO : professorList) {
                ProjectInstructorRole role = new ProjectInstructorRole();

                // ค้นหาข้อมูลอาจารย์ในฐานข้อมูล
                Optional<Instructor> instructorOptional = instructorRepository.findByProfessorName(professorDTO.getProfessorName());

                // ตรวจสอบว่า Optional มีค่าหรือไม่
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
        }

        // เพิ่มข้อมูลนักศึกษา
        List<StudentProjectDTO> studentList = projectDetailsDTO.getStudentList();
        if (studentList != null) {
            for (StudentProjectDTO studentDTO : studentList) {
                StudentProject studentProject = new StudentProject();
                // ค้นหาข้อมูลนักศึกษาในฐานข้อมูล
                Student student = studentRepository.findByStudentId(studentDTO.getStudentId());
                if (student != null) {
                    studentProject.setProject(newProject);  // ตั้งค่าโปรเจกต์
                    studentProject.setStudent(student);    // ตั้งค่านักศึกษา
                    studentProject.setStatus(studentDTO.getStatus() != null ? studentDTO.getStatus() : "Active"); // ตั้งค่าสถานะ
                    studentProjectRepository.save(studentProject);
                } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found: " + studentDTO.getStudentId());
                }
            }
        }

        // ส่งกลับข้อมูลโปรเจกต์ใหม่ในรูปแบบ DTO
        return new ProjectDetailsDTO(
                newProject.getProjectId(),
                newProject.getProjectTitle(),
                professorList,
                newProject.getProjectDescription(),
                newProject.getProgram(),
                studentList
        );
    }
}
