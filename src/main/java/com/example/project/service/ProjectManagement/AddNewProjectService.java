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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AddNewProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectInstructorRoleRepository projectInstructorRoleRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentProjectRepository studentProjectRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Transactional
    public void addNewProject(ProjectDetailsDTO projectDetailsDTO) {
        // ดึงปีปัจจุบัน
        int currentYear = LocalDateTime.now().getYear();

        // ดึงค่า program จากฟอร์ม
        String program = projectDetailsDTO.getProgram();

        // หาเลขลำดับล่าสุดจากฐานข้อมูล (ใช้รหัส projectId ล่าสุด)
        String lastProjectId = getLastProjectId(program);  // ใช้ method นี้เพื่อหาค่าล่าสุด
        String newProjectId = generateNewProjectId(lastProjectId, program, currentYear); // สร้าง Project ID ใหม่

        // สร้างโปรเจกต์ใหม่
        Project newProject = new Project();
        newProject.setProjectId(newProjectId);  // ตั้งค่า projectId ที่สร้างใหม่
        newProject.setProjectTitle(projectDetailsDTO.getProjectTitle());
        newProject.setProjectDescription(projectDetailsDTO.getProjectDescription());
        newProject.setProgram(projectDetailsDTO.getProgram());

        // บันทึกโปรเจกต์ใหม่ลงในฐานข้อมูล
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

                // สร้างรหัส ProjectInstructorRole ใหม่
                String instructorRoleId = generateNewProjectId(lastProjectId, program, currentYear);  // ใช้ฟังก์ชันเดียวกันในการสร้าง ID ใหม่
                role.setProjectInstructorRoleId(instructorRoleId);  // ตั้งค่า ID สำหรับ ProjectInstructorRole

                // ตั้งค่าอาจารย์ใน ProjectInstructorRole
                role.setProjectIdRole(newProject);  // ตั้งค่าโปรเจกต์
                role.setInstructor(instructor);     // ตั้งค่าอาจารย์
                role.setRole(professorDTO.getRole());  // ตั้งค่าบทบาท
                role.setAssignDate(LocalDateTime.now());  // ตั้งค่าวันที่ที่มอบหมาย

                // บันทึกข้อมูลอาจารย์ที่ปรึกษาในฐานข้อมูล
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
    }

    private String generateNewProjectId(String lastProjectId, String program, int year) {
        // สร้างรหัสโปรเจกต์ใหม่จากลำดับล่าสุดที่พบในฐานข้อมูล
        String prefix = program;  // ค่า program (DST, ICT)
        String yearPart = String.valueOf(year);
        String sequencePart = "01";  // ค่าเริ่มต้นลำดับ

        if (lastProjectId != null && lastProjectId.startsWith(prefix) && lastProjectId.contains("SP")) {
            String lastSequence = lastProjectId.substring(lastProjectId.length() - 2);  // รับลำดับสุดท้ายจากรหัสโปรเจกต์
            int newSequence = Integer.parseInt(lastSequence) + 1;
            sequencePart = String.format("%02d", newSequence);  // เพิ่มลำดับใหม่และจัดรูปแบบให้เป็น 2 หลัก
        }

        // รวมรหัสโปรเจกต์ใหม่
        return String.format("%s SP%s-%s", prefix, yearPart, sequencePart);
    }

    // ฟังก์ชันในการดึงรหัสโปรเจกต์ล่าสุด
    public String findLastProjectId() {
        // ค้นหาค่ารหัสโปรเจกต์ล่าสุดจากฐานข้อมูล
        String lastProjectId = projectRepository.findLastProjectId();

        // ถ้ามีรหัสโปรเจกต์ล่าสุดให้ส่งคืน ถ้าไม่มีก็ส่งค่าว่าง
        return (lastProjectId != null) ? lastProjectId : "SP0000";  // กำหนดรหัสโปรเจกต์เริ่มต้น
    }

    // ฟังก์ชันในการดึงนักศึกษาที่ยังไม่มีโปรเจกต์
    public List<Student> getStudentsWithoutProject() {
        // ดึงข้อมูลนักศึกษาทั้งหมด
        List<Student> allStudents = studentRepository.findAll();

        // ค้นหานักศึกษาที่ไม่มีการเชื่อมโยงกับโปรเจกต์ใน StudentProject
        List<Student> studentsWithoutProject = allStudents.stream()
                .filter(student -> !studentProjectRepository.existsByStudentStudentId(student.getStudentId()))  // ตรวจสอบว่าไม่มีการเชื่อมโยง
                .collect(Collectors.toList());

        return studentsWithoutProject;
    }

    public List<Instructor> getInstructorsWithoutProject() {
        // ดึงข้อมูลอาจารย์ทั้งหมด
        List<Instructor> allInstructors = instructorRepository.findAll();

        // ค้นหาข้อมูลอาจารย์ที่ไม่มีการเชื่อมโยงกับโปรเจกต์ใน ProjectInstructorRole
        List<Instructor> instructorsWithoutProject = allInstructors.stream()
                .filter(instructor -> !projectInstructorRoleRepository.existsByInstructorProfessorId(instructor.getProfessorId()))  // ตรวจสอบว่าไม่มีการเชื่อมโยง
                .collect(Collectors.toList());

        return instructorsWithoutProject;
    }

    // ฟังก์ชันนี้จะดึงรหัสโปรเจกต์ล่าสุดจากฐานข้อมูล
    public String getLastProjectId(String program) {
        // ดึงรหัสโปรเจกต์ล่าสุดจากฐานข้อมูล
        String lastProjectId = projectInstructorRoleRepository.findLatestInstructorId(); // ค้นหาจากรหัสโปรเจกต์ที่เริ่มต้นด้วย program

        if (lastProjectId == null) {
            return "INST001";  // เริ่มต้นที่ SP001 หากไม่มีข้อมูล
        }

        // ดึงลำดับจากรหัสโปรเจกต์ล่าสุด
        String lastSequence = lastProjectId.substring(lastProjectId.length() - 3);  // ดึง 3 ตัวสุดท้ายจากรหัสโปรเจกต์ (เช่น 99)
        int newSequence = Integer.parseInt(lastSequence) + 1;
        String newSequenceFormatted = String.format("%03d", newSequence);  // ทำให้ลำดับเป็น 3 หลัก

        // สร้างรหัสโปรเจกต์ใหม่
        return "INST" + newSequenceFormatted;
    }


}
