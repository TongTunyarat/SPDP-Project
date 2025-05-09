package com.example.project.service.ProjectManagement;

import com.example.project.DTO.projectManagement.NewProjectDTO;
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
import java.util.*;
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
    public String createProject(NewProjectDTO dto) {

        // --- เช็คขอบเขตบทบาท ---
        List<ProfessorRoleDTO> profs = Optional.ofNullable(dto.getProfessorList()).orElse(List.of());
        List<String> warnings = new ArrayList<>();

        long advCount = profs.stream()
                .filter(p -> "Advisor".equalsIgnoreCase(p.getRole()))
                .count();
        if (advCount > 1) {
            warnings.add("ระบุ Advisor ได้ไม่เกิน 1 ท่าน (พบ " + advCount + " ท่าน)");
        }

        long commCount = profs.stream()
                .filter(p -> "Committee".equalsIgnoreCase(p.getRole()))
                .count();
        if (commCount > 2) {
            warnings.add("ระบุ Committee ได้ไม่เกิน 2 ท่าน (พบ " + commCount + " ท่าน)");
        }

        List<String> names = profs.stream()
                .map(ProfessorRoleDTO::getProfessorName)
                .filter(name -> name != null && !name.isBlank())
                .collect(Collectors.toList());
        Set<String> uniqNames = new HashSet<>(names);
        if (uniqNames.size() != names.size()) {
            warnings.add("พบชื่ออาจารย์ซ้ำกันในรายการ");
        }

        List<String> ids = profs.stream()
                .map(ProfessorRoleDTO::getProfessorId)
                .filter(id -> id != null && !id.isBlank())
                .collect(Collectors.toList());
        Set<String> uniqIds = new HashSet<>(ids);
        if (uniqIds.size() != ids.size()) {
            warnings.add("พบรหัสอาจารย์ซ้ำกันในรายการ");
        }


        if (!warnings.isEmpty()) {
            // โยน exception ทีเดียว พร้อม list ของ warnings
            throw new IllegalArgumentException(String.join(";", warnings));
        }

        if (dto.getStudentList() != null) {
            List<String> stuIds = dto.getStudentList().stream()
                    .map(StudentProjectDTO::getStudentId)
                    .collect(Collectors.toList());
            Set<String> uniqStu = new HashSet<>(stuIds);
            if (uniqStu.size() != stuIds.size()) {
                throw new IllegalArgumentException("มีนักศึกษาซ้ำในรายการ กรุณาตรวจสอบใหม่");
            }
        }

        // 1) Gen projectId
        String newProjId = generateNewProjectId(dto.getProgram(), dto.getSemester());
        LocalDateTime now = LocalDateTime.now();

        // 2) สร้าง Project
        Project p = new Project();
        p.setProjectId(newProjId);
        p.setProgram(dto.getProgram());
        p.setSemester(dto.getSemester());
        p.setProjectTitle(dto.getProjectTitle());
        p.setProjectCategory(dto.getProjectCategory());
        p.setProjectDescription(dto.getProjectDescription());
        p.setRecordedOn(now);
        p.setEditedOn(now);
        projectRepository.save(p);

        // 3) สร้าง StudentProject
        int counter = Integer.parseInt(generateNextStudentPjId());
        for (StudentProjectDTO sDto : dto.getStudentList()) {
            if (sDto.getStudentId() == null || sDto.getStudentId().isBlank()) continue;
            Student stu = studentRepository.findById(sDto.getStudentId())
                    .orElseThrow(() -> new IllegalStateException("Student not found: " + sDto.getStudentId()));
            // (ถ้าต้องเช็ค name ก็เช็ค sDto.getStudentName() เพิ่มได้)
            StudentProject sp = new StudentProject();
            sp.setStudent(stu);
            sp.setProject(p);
            sp.setStatus("Active");
            sp.setStudentPjId("SP" + String.format("%03d", counter++));
            studentProjectRepository.save(sp);
        }

        // 4) สร้าง Instructor Roles ทั้ง 4 ประเภท
        now = LocalDateTime.now();  // อัพเดต timestamp ใหม่
        // นำ professorList ทั้งหมดมาอัปโหลด
        addRoles(dto.getProfessorList(), p, now);

        return newProjId;
    }

    private void addRoles(
            List<ProfessorRoleDTO> professorList,
            Project project,
            LocalDateTime assignDate
    ) {
        if (professorList == null) return;

        // Process all professor roles in the list
        for (ProfessorRoleDTO prof : professorList) {
            String name = prof.getProfessorName();  // สมมติ DTO มีฟิลด์นี้
            if (name == null || name.isBlank()) continue;

            // ค้นหา Instructor ตามชื่อ
            Instructor instr = instructorRepository
                    .findByProfessorName(name)
                    .orElseThrow(() -> new IllegalStateException("Instructor not found: " + name));

            if (instr.getProfessorId() == null) {
                instr.setProfessorId(generateNextInstructorId());
                instructorRepository.save(instr);
            }

            // สร้าง ProjectInstructorRole สำหรับ professor นี้
            ProjectInstructorRole pir = new ProjectInstructorRole();
            pir.setInstructorId(generateNextInstructorId());
            pir.setAssignDate(assignDate);
            pir.setRole(prof.getRole()); // ใช้ role ที่ได้จาก DTO
            pir.setProjectIdRole(project);
            pir.setInstructor(instr);
            projectInstructorRoleRepository.save(pir);


        }

    }


    private String generateNewProjectId(String program, String semester) {
        int nextNum = generateNextProjectNumber(program, semester);
        return program + " SP" + semester
                + "-" + String.format("%02d", nextNum);
    }

    private int generateNextProjectNumber(String program, String year) {
        String latestProjectId = projectRepository.findLatestProjectIdByProgramAndYear(program, year);
        if (latestProjectId == null || latestProjectId.isEmpty()) {
            return 1;
        }
        String[] parts = latestProjectId.split("-");
        if (parts.length < 2) {
            return 1;
        }
        try {
            int latestNumber = Integer.parseInt(parts[1]);
            return latestNumber + 1;
        } catch (NumberFormatException ex) {
            return 1;
        }
    }

    private String generateNextStudentPjId() {
        String latestId = studentProjectRepository.findLatestStudentPjId();
        if (latestId == null || latestId.isEmpty()) {
            return "01";
        }
        String numericPart = latestId.substring(2);
        int nextNumber = Integer.parseInt(numericPart) + 1;
        int width = numericPart.length();
        return String.format("%0" + width + "d", nextNumber);
    }


    private String generateNextInstructorId() {
        // ค้นหาค่ารหัสล่าสุดที่มีอยู่ในฐานข้อมูล
        String latestId = projectInstructorRoleRepository.findLatestInstructorId();

        // ถ้าไม่พบรหัสล่าสุด (กรณีฐานข้อมูลยังว่างเปล่า)
        if (latestId == null) {
            return "INST001";  // เริ่มต้นที่ INST001
        }

        // เอารหัสล่าสุดออกมาจาก INSTxxxx และเพิ่ม 1
        String numericPart = latestId.substring(4);  // เอาส่วนเลขออกจาก INSTxxxx
        int nextId = Integer.parseInt(numericPart) + 1;  // เพิ่ม 1
        return String.format("INST%03d", nextId);  // ใช้ String.format เพื่อให้รหัสใหม่มีรูปแบบเหมือนเดิม เช่น INST002, INST003, ...
    }


    // ฟังก์ชันในการดึงรหัสโปรเจกต์ล่าสุด ✅
    public String findLatestProjectId(String program, String semester) {
        return projectRepository.findLatestProjectIdByProgramAndYear(program, semester);
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




}
