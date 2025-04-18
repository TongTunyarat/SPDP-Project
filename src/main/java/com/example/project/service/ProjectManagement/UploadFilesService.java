package com.example.project.service.ProjectManagement;

import com.example.project.entity.*;
import com.example.project.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UploadFilesService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentProjectRepository studentProjectRepository;

    @Autowired
    private ProjectInstructorRoleRepository projectInstructorRoleRepository;

    @Autowired
    private InstructorRepository instructorRepository;


//    public Map<String, Object> uploadFile(MultipartFile file) {
//        Map<String, Object> response = new HashMap<>();
//        List<String> errorLogs = new ArrayList<>();
//        List<Student> students = new ArrayList<>();
//        List<Project> projects = new ArrayList<>();
//        List<StudentProject> studentProjects = new ArrayList<>();
//
//        try {
//            if (file.getOriginalFilename().endsWith(".csv")) {
//                processCSV(file, students, projects, studentProjects, errorLogs);
//            } else if (file.getOriginalFilename().endsWith(".xlsx")) {
//                processExcel(file, students, projects, studentProjects, errorLogs);
//            } else {
//                throw new IllegalArgumentException("Unsupported file format");
//            }
//
//            saveDataToDatabase(students, projects, studentProjects); // ✅ ส่ง studentProjects เข้าไป
//
//            response.put("message", "File processed successfully");
//            response.put("errors", errorLogs);
//        } catch (Exception e) {
//            System.out.println("Error processing file: " + e.getMessage());
//            e.printStackTrace();
//            response.put("message", "File processing failed");
//            response.put("errors", List.of(e.getMessage()));
//        }
//
//        return response;
//    }
//
//    private void processCSV(MultipartFile file, List<Student> students, List<Project> projects, List<StudentProject> studentProjects, List<String> errorLogs) {
//        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
//            List<String[]> lines = reader.readAll();
//            boolean isFirstLine = true;
//            String currentProjectId = null;  // ตัวแปรเก็บ Project ID ของโปรเจกต์ปัจจุบัน
//            int studentCount = 0;  // ตัวแปรนับจำนวนของนักศึกษาที่เชื่อมโยงกับ Project ID
//            List<StudentProjectDTO> studentList = new ArrayList<>(); // รายชื่อนักศึกษาในโปรเจกต์เดียวกัน
//            List<ProfessorRoleDTO> professorList = new ArrayList<>(); // รายชื่ออาจารย์
//
//            for (String[] data : lines) {
//                if (isFirstLine) {
//                    isFirstLine = false;
//                    continue;
//                }
//
//                // ตรวจสอบให้มั่นใจว่าข้อมูลมีจำนวนคอลัมน์ที่ถูกต้อง
//                if (data.length < 10) {
//                    errorLogs.add("Invalid CSV format: " + Arrays.toString(data));
//                    continue;
//                }
//
//                // ข้ามแถวตัวอย่าง
//                String projectId = data[0].trim();
//                if (projectId.equals("XXX SP20XX-XX")) {
//                    errorLogs.add("Skipping example row with Project ID: " + Arrays.toString(data));
//                    continue;  // ข้ามแถวนี้ไป
//                }
//
//                // ถ้า Project ID เป็นค่าว่าง ให้ใช้ Project ID จากแถวก่อนหน้า
//                if (projectId.isEmpty() && currentProjectId != null) {
//                    data[0] = currentProjectId;  // คัดลอก Project ID จากแถวก่อนหน้า
//                } else {
//                    currentProjectId = projectId;
//                    studentCount = 0;  // เริ่มต้นการนับนักศึกษาใหม่เมื่อ Project ID เปลี่ยน
//                }
//
//                // เพิ่มตัวนับนักศึกษาในโปรเจกต์เดียวกัน
//                studentCount++;
//
//                // จำกัดให้นักศึกษาในโปรเจกต์เดียวกันมีได้สูงสุด 3 คน
//                if (studentCount > 3) {
//                    errorLogs.add("Exceeded 3 students for the same Project ID: " + Arrays.toString(data));
//                    continue;  // ข้ามแถวนี้หากมีนักศึกษามากกว่า 3 คนในโปรเจกต์เดียวกัน
//                }
//
//                // ตรวจสอบข้อมูลที่จำเป็น เช่น Student ID และ Student Name
//                if (data[6] == null || data[6].isEmpty() || data[7] == null || data[7].isEmpty()) {
//                    errorLogs.add("Missing required Student fields: " + Arrays.toString(data));
//                    continue;
//                }
//
//                // สร้างและเพิ่มข้อมูลนักศึกษาลงใน studentList
//                StudentProjectDTO studentDTO = new StudentProjectDTO(
//                        data[6].trim(),      // studentId
//                        data[7].trim(),      // studentName
//                        data[9].trim(),      // section
//                        data[10].trim(),     // track
//                        "Active"             // status
//                );
//                studentList.add(studentDTO);
//
//                // หากพบว่าแถวนี้เป็นข้อมูลของโปรเจกต์แรก ให้เพิ่มอาจารย์ลงใน professorList
//                if (studentCount == 1) {
//                    // สร้าง ProfessorRoleDTO สำหรับ Advisor
//                    if (!data[12].trim().isEmpty()) {
//                        ProfessorRoleDTO professorDTO = new ProfessorRoleDTO(data[12].trim(), "Advisor");
//                        professorList.add(professorDTO);
//                    }
//
//                    // สร้าง ProfessorRoleDTO สำหรับ Co-Advisor
//                    if (!data[13].trim().isEmpty()) {
//                        ProfessorRoleDTO professorDTO = new ProfessorRoleDTO(data[13].trim(), "Co-Advisor");
//                        professorList.add(professorDTO);
//                    }
//
//                    // สร้าง ProfessorRoleDTO สำหรับ Committee
//                    if (!data[14].trim().isEmpty()) {
//                        ProfessorRoleDTO professorDTO = new ProfessorRoleDTO(data[14].trim(), "Committee");
//                        professorList.add(professorDTO);
//                    }
//
//                    // สร้าง ProfessorRoleDTO สำหรับ Poster-Committee
//                    if (!data[15].trim().isEmpty()) {
//                        ProfessorRoleDTO professorDTO = new ProfessorRoleDTO(data[15].trim(), "Poster-Committee");
//                        professorList.add(professorDTO);
//                    }
//                }
//
//                // ส่งข้อมูลไปยัง mapProjectToEntities เพื่อทำการแปลงข้อมูล
//                mapProjectToEntities(data, students, projects, studentProjects, errorLogs);
//            }
//
//            // สร้าง Project จากข้อมูลใน ProjectDetailsResponseDTO
//            Project project = new Project();
//            project.setProjectId(currentProjectId);
//            project.setProjectTitle(studentList.get(0).getStudentName());  // Set project title from first student
//            project.setProjectDescription(studentList.get(0).getStatus()); // Set project description from first student
//            project.setProgram(studentList.get(0).getSection());  // Set program from first student
//
//            // เพิ่มเวลาปัจจุบันสำหรับ recorded_on และ edited_on
//            LocalDateTime now = LocalDateTime.now();
//            project.setRecordedOn(now);
//            project.setEditedOn(now);
//
//            // แปลง professorList (ProfessorRoleDTO) ไปเป็น ProjectInstructorRole
//            List<ProjectInstructorRole> projectInstructorRoles = new ArrayList<>();
//            for (ProfessorRoleDTO professorRoleDTO : professorList) {
//                // สร้าง Instructor จาก ProfessorRoleDTO
//                Instructor instructor = new Instructor();
//                instructor.setProfessorName(professorRoleDTO.getProfessorName());
//
//                // สร้าง ProjectInstructorRole และตั้งค่า Instructor
//                ProjectInstructorRole projectInstructorRole = new ProjectInstructorRole();
//                projectInstructorRole.setInstructor(instructor); // ตั้งค่าความสัมพันธ์กับ Instructor
//                projectInstructorRole.setRole(professorRoleDTO.getRole());
//
//                projectInstructorRoles.add(projectInstructorRole);
//            }
//
//            // เพิ่มรายชื่อนักศึกษาและอาจารย์ในโปรเจกต์
//            project.setStudentProjects(studentProjects);  // รายชื่อนักศึกษา
//            project.setProjectInstructorRoles(projectInstructorRoles);  // รายชื่ออาจารย์
//
//            // เพิ่ม Project ลงใน List ของ Project
//            projects.add(project);  // เพิ่ม Project ลงในลิสต์
//
//
//        } catch (Exception e) {
//            System.out.println("Error processing CSV: " + e.getMessage());
//            errorLogs.add("Error processing CSV: " + e.getMessage());
//        }
//    }
//
//
//    private void mapProjectToEntities(String[] data, List<Student> students, List<Project> projects, List<StudentProject> studentProjects, List<String> errorLogs) {
//        try {
//            System.out.println("📌 Raw data from file: " + Arrays.toString(data));
//
//            // ตรวจสอบความยาวของข้อมูล
//            if (data.length < 11) {
//                errorLogs.add("Invalid data format: " + Arrays.toString(data));
//                return;
//            }
//
//            // ตรวจสอบว่า Project ID ไม่เป็นค่าว่าง
//            String projectId = data[0].trim();
//            if (projectId.isEmpty()) {
//                errorLogs.add("Missing required Project ID: " + Arrays.toString(data));
//                return;
//            }
//
//            // ตรวจสอบข้อมูล Project
//            String projectTitle = data[1].trim();
//            String projectDescription = data[2].trim();
//            if (projectTitle.isEmpty() || projectDescription.isEmpty()) {
//                errorLogs.add("Missing required Project fields (Title or Description): " + Arrays.toString(data));
//                return;
//            }
//
//            // สร้าง Project Entity
//            Project project = new Project();
//            project.setProjectId(projectId);
//            project.setProjectTitle(projectTitle);
//            project.setProjectDescription(projectDescription);
//            project.setProgram(data[0].split(" ")[0]); // Program ใช้จาก Project ID
//
//            // เพิ่ม Project ลงใน List ของ Project
//            projects.add(project);  // เพิ่ม Project ลงในลิสต์
//
//            // สร้าง Student จากข้อมูล (Student ID และ Student Name สามารถเป็นค่าว่างได้)
//            Student student = new Student();
//            student.setStudentId(data[6].trim());
//            student.setStudentName(data[7].trim());
//            student.setProgram(data[0].split(" ")[0]);
//            student.setSection(data[9].isEmpty() ? 0 : Byte.parseByte(data[9].trim()));  // ถ้า Section เป็นค่าว่าง ให้เป็น 0
//            student.setTrack(data[10].trim().isEmpty() ? "Unknown" : data[10].trim()); // ถ้า Track เป็นค่าว่าง ให้เป็น "Unknown"
//            students.add(student);  // เพิ่ม Student ลงในลิสต์
//
//            // สร้างความสัมพันธ์ระหว่าง Student และ Project
//            StudentProject studentProject = new StudentProject(student, project);
//            studentProject.setStudentPjId(UUID.randomUUID().toString());
//            studentProjects.add(studentProject);
//
//            System.out.println("✅ Mapped Project: " + project.getProjectId() + " - " + project.getProjectTitle());
//
//        } catch (Exception e) {
//            errorLogs.add("Data mapping error: " + Arrays.toString(data) + " -> " + e.getMessage());
//            System.out.println("Data mapping error: " + Arrays.toString(data) + " -> " + e.getMessage());
//        }
//    }


    private void saveDataToDatabase(List<Student> students, List<Project> projects, List<StudentProject> studentProjects) {
        // บันทึกข้อมูลทั้งหมดที่ผ่านการตรวจสอบแล้ว
        studentRepository.saveAll(students);  // บันทึก Student
        projectRepository.saveAll(projects);  // บันทึก Project
        studentProjectRepository.saveAll(studentProjects);  // บันทึก StudentProject
    }


    // ----------------- ข้างล่างนี้ใช่ได้ ----------------- //

//    public void processCsvFile(MultipartFile file) throws Exception {
//        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
//            String line;
//            int rowIndex = 0;
//
//            while ((line = br.readLine()) != null) {
//                rowIndex++;
//                // Skip the first 8 rows
//                if (rowIndex < 7) continue;
//
//                String[] values = line.split(",");
//
//                // ตรวจสอบว่ามีข้อมูลเพียงพอก่อนการเข้าถึง
//                if (values.length < 15) {  // ปรับค่าตามจำนวนคอลัมน์ในไฟล์ของคุณ
//                    // เพิ่มการบันทึก log หรือแสดงข้อความเตือน
//                    System.out.println("Invalid row (less than expected columns): " + line);
//                    continue;  // ข้ามแถวนี้ไป
//                }
//
//                // Extract data from CSV row
//                String projectId = values[0].trim();
//                String projectTitle = values[1].trim();
//                String projectDescription = values[2].trim();
//                String studentId = values[3].trim();
//                String studentName = values[4].trim();
//                String program = values[5].trim();
//                String section = values[6].trim();
//                String track = values[7].trim();
//                String advisor = values[10].trim();
//                String coAdvisor = values[11].trim();
//                String committee = values[12].trim();
//                String posterCommittee = values[13].trim();
//
//                // Create DTO for ProjectDetailsResponseDTO
//                ProjectDetailsResponseDTO projectDTO = new ProjectDetailsResponseDTO(
//                        projectId, projectTitle, new ArrayList<>(), projectDescription, program, new ArrayList<>(),
//                        new ArrayList<>(), new ArrayList<>(), new ArrayList<>()
//                );
//
//                // Process Project entity
//                Project project = processProject(projectId, projectTitle, projectDescription, program);
//
//                // Process StudentProject entity
//                processStudentProject(studentId, studentName, projectId, section, track, projectDTO);
//
//                // Process ProjectInstructorRole entity
//                processProjectInstructorRole(advisor, coAdvisor, committee, posterCommittee, projectId, projectDTO);
//            }
//        } catch (IOException e) {
//            throw new Exception("Error reading CSV file: " + e.getMessage());
//        }
//    }
//
//    private Project processProject(String projectId, String projectTitle, String projectDescription, String program) {
//        Optional<Project> existingProject = projectRepository.findById(projectId);
//        if (!existingProject.isPresent()) {
//            int currentYear = LocalDate.now().getYear();
//
//            Project project = new Project(projectId, program, String.valueOf(currentYear), projectTitle, "Develop", projectDescription, LocalDateTime.now(), LocalDateTime.now(), null, null);
//            return projectRepository.save(project);
//        }
//        return existingProject.get();
//    }
//
//    private void processStudentProject(String studentId, String studentName, String projectId, String section, String track, ProjectDetailsResponseDTO projectDTO) {
//        // ค้นหา Student จากฐานข้อมูล
//        Optional<Student> existingStudent = studentRepository.findById(studentId);
//        if (existingStudent.isPresent()) {
//            // ค้นหา Project จากฐานข้อมูลโดยใช้ projectId
//            Optional<Project> existingProject = projectRepository.findById(projectId);
//
//            // หากพบ Project
//            if (existingProject.isPresent()) {
//                StudentProject studentProject = new StudentProject();
//                studentProject.setStudent(existingStudent.get());
//                studentProject.setProject(existingProject.get());  // ตั้งค่า Project เป็นอ็อบเจ็กต์ Project ที่ค้นพบ
//                studentProject.setStatus("active");
//                studentProjectRepository.save(studentProject);
//
//                // เพิ่มข้อมูลนักศึกษาใน DTO
//                projectDTO.getStudentList().add(new StudentProjectDTO(studentId, studentName, section, track, studentProject.getStatus()));
//            }
//        }
//    }
//
//
//    private void processProjectInstructorRole(String advisor, String coAdvisor, String committee, String posterCommittee, String projectId, ProjectDetailsResponseDTO projectDTO) {
//        // ค้นหา Project ที่มี ID ตรงกับในฐานข้อมูล
//        Optional<Project> existingProject = projectRepository.findById(projectId);
//        if (!existingProject.isPresent()) {
//            // ถ้าไม่พบ Project ที่ตรงกับ projectId ให้หยุดการทำงาน
//            throw new IllegalArgumentException("Project not found with ID: " + projectId);
//        }
//
//        // ค้นหาอาจารย์จากชื่อในฐานข้อมูล
//        Optional<Instructor> existingAdvisor = instructorRepository.findByProfessorName(advisor);
//        Optional<Instructor> existingCoAdvisor = instructorRepository.findByProfessorName(coAdvisor);
//        Optional<Instructor> existingCommittee = instructorRepository.findByProfessorName(committee);
//        Optional<Instructor> existingPosterCommittee = instructorRepository.findByProfessorName(posterCommittee);
//
//        // สร้าง ProjectInstructorRole สำหรับอาจารย์ในบทบาทต่าง ๆ
//        if (existingAdvisor.isPresent()) {
//            ProjectInstructorRole projectInstructorRole = new ProjectInstructorRole();
//            projectInstructorRole.setProjectIdRole(existingProject.get());  // ใช้ Project ที่มี ID ตรงกันจากฐานข้อมูล
//            projectInstructorRole.setInstructor(existingAdvisor.get());  // กำหนดอาจารย์เป็น Advisor
//            projectInstructorRole.setRole("Advisor");  // บทบาทเป็น Advisor
//            projectInstructorRole.setAssignDate(LocalDateTime.now());  // กำหนดวันที่มอบหมาย
//            projectInstructorRoleRepository.save(projectInstructorRole);
//
//            // เพิ่มข้อมูลอาจารย์และบทบาทใน DTO
//            projectDTO.getAdvisors().add(new ProfessorRoleDTO(existingAdvisor.get().getProfessorName(), "Advisor"));
//        }
//
//        if (existingCoAdvisor.isPresent()) {
//            ProjectInstructorRole projectInstructorRole = new ProjectInstructorRole();
//            projectInstructorRole.setProjectIdRole(existingProject.get());  // ใช้ Project ที่มี ID ตรงกันจากฐานข้อมูล
//            projectInstructorRole.setInstructor(existingCoAdvisor.get());  // กำหนดอาจารย์เป็น Co-Advisor
//            projectInstructorRole.setRole("Co-Advisor");  // บทบาทเป็น Co-Advisor
//            projectInstructorRole.setAssignDate(LocalDateTime.now());  // กำหนดวันที่มอบหมาย
//            projectInstructorRoleRepository.save(projectInstructorRole);
//
//            // เพิ่มข้อมูลอาจารย์และบทบาทใน DTO
//            projectDTO.getCoAdvisors().add(new ProfessorRoleDTO(existingCoAdvisor.get().getProfessorName(), "Co-Advisor"));
//        }
//
//        if (existingCommittee.isPresent()) {
//            ProjectInstructorRole projectInstructorRole = new ProjectInstructorRole();
//            projectInstructorRole.setProjectIdRole(existingProject.get());  // ใช้ Project ที่มี ID ตรงกันจากฐานข้อมูล
//            projectInstructorRole.setInstructor(existingCommittee.get());  // กำหนดอาจารย์เป็น Committee
//            projectInstructorRole.setRole("Committee");  // บทบาทเป็น Committee
//            projectInstructorRole.setAssignDate(LocalDateTime.now());  // กำหนดวันที่มอบหมาย
//            projectInstructorRoleRepository.save(projectInstructorRole);
//
//            // เพิ่มข้อมูลอาจารย์และบทบาทใน DTO
//            projectDTO.getCommittees().add(new ProfessorRoleDTO(existingCommittee.get().getProfessorName(), "Committee"));
//        }
//
//        if (existingPosterCommittee.isPresent()) {
//            ProjectInstructorRole projectInstructorRole = new ProjectInstructorRole();
//            projectInstructorRole.setProjectIdRole(existingProject.get());  // ใช้ Project ที่มี ID ตรงกันจากฐานข้อมูล
//            projectInstructorRole.setInstructor(existingPosterCommittee.get());  // กำหนดอาจารย์เป็น Poster-Committee
//            projectInstructorRole.setRole("Poster-Committee");  // บทบาทเป็น Poster-Committee
//            projectInstructorRole.setAssignDate(LocalDateTime.now());  // กำหนดวันที่มอบหมาย
//            projectInstructorRoleRepository.save(projectInstructorRole);
//
//            // เพิ่มข้อมูลอาจารย์และบทบาทใน DTO
//            projectDTO.getCommittees().add(new ProfessorRoleDTO(existingPosterCommittee.get().getProfessorName(), "Poster-Committee"));
//        }
//    }

    // โค้ดเก่าถึงตรงนี้ที่ใช้ได้


    // ----------------- Value from Dropdown ----------------- //
    // Main method to process CSV file
//    public void processCsvFile(MultipartFile file, String uploadType) throws Exception {
//        switch (uploadType) {
//            case "projectDetails":
//                processProjectAndStudent(file);
//                break;
//            case "projectCommittee":
//                processProjectInstructor(file);
//                break;
//            case "projectPosterCommittee":
//                processProjectInstructor(file);
//                break;
//            default:
//                throw new IllegalArgumentException("Invalid upload type selected");
//        }
//    }


    // ----------------- Function Upload Project Details ----------------- //
//    private void processProjectAndStudent(MultipartFile file) throws Exception {
//        // (โค้ด processProjectAndStudent ที่คุณให้มา)
//        // ไม่เปลี่ยนแปลง เพราะเป็นส่วนที่ทำการบันทึกลงฐานข้อมูล
//        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
//            String line;
//            int rowIndex = 0;
//            int currentYear = LocalDate.now().getYear();
//
//            // Map สำหรับเก็บเลข sequence สำหรับแต่ละคู่ (program, year)
//            Map<String, Integer> projectNumberCounters = new HashMap<>();
//            // Map สำหรับเก็บโปรเจกต์ที่สร้างไว้แล้ว (key: projectId ใหม่)
//            Map<String, Project> createdProjects = new HashMap<>();
//            // ตัวแปรสำหรับเก็บโปรเจกต์ปัจจุบันในกลุ่ม (หากแถวต่อมาขาดข้อมูลโปรเจกต์)
//            String currentProjectId = null;
//
//            // ใช้สำหรับสร้างรหัส student_pj_id ใหม่ (ตามลำดับ)
//            int studentIdCounter = Integer.parseInt(generateNextStudentPjId());
//
//            // วนลูปอ่านไฟล์ CSV (สมมุติว่า header อยู่ที่ 8 แถวแรก)
//            while ((line = br.readLine()) != null) {
//                rowIndex++;
//                if (rowIndex < 9) continue; // ข้าม header
//
//                if (line.trim().isEmpty()) continue;
//                String[] values = line.split(",");
//                if (values.length < 9) {
//                    System.out.println("Skipping row " + rowIndex + " due to insufficient columns.");
//                    continue;
//                }
//
//                // อ่านข้อมูลจากไฟล์ตามตำแหน่ง (0-based index)
//                String fileProjectId = values[1].trim();
//                String projectTitle = values[2].trim();
//                String projectDescription = values[3].trim();
//                String projectCategory = values[4].trim();
//                String studentId = values[5].trim();
//                String studentName = values[6].trim();
//                String program = values[7].trim();
//                String advisor = values[8].trim();
//
//                // ตรวจสอบข้อมูลนักศึกษาจำเป็นต้องมี StudentID และ StudentName
//                if (studentId.isEmpty() || studentName.isEmpty()) {
//                    System.out.println("Skipping row " + rowIndex + " due to missing student information.");
//                    continue;
//                }
//
//                // --------------- Process Project --------------- //
//                if (!fileProjectId.isEmpty() || !projectTitle.isEmpty() ||
//                        !projectDescription.isEmpty() || !projectCategory.isEmpty()) {
//
//                    // กำหนดเลข sequence ของโปรเจกต์ตามคู่ (program, year)
//                    String key = program + "_" + currentYear;
//                    int projectNumberCounter;
//                    if (projectNumberCounters.containsKey(key)) {
//                        projectNumberCounter = projectNumberCounters.get(key) + 1;
//                    } else {
//                        projectNumberCounter = generateNextProjectNumber(program, String.valueOf(currentYear));
//                    }
//                    projectNumberCounters.put(key, projectNumberCounter);
//
//                    // สร้าง ProjectId ใหม่ในรูปแบบ "Program SP<Year>-<sequence>"
//                    String newProjectId = program + " SP" + currentYear + "-" + String.format("%02d", projectNumberCounter);
//
//                    // สร้าง Record สำหรับ Project
//                    Project project = new Project();
//                    project.setProjectId(newProjectId);
//                    project.setProgram(program);
//                    project.setSemester(String.valueOf(currentYear));
//                    project.setProjectTitle(projectTitle);
//                    project.setProjectCategory(projectCategory);
//                    project.setProjectDescription(projectDescription);
//                    project.setRecordedOn(LocalDateTime.now());
//                    project.setEditedOn(LocalDateTime.now());
//
//                    projectRepository.save(project);
//                    createdProjects.put(newProjectId, project);
//                    currentProjectId = newProjectId;
//                } else {
//                    if (currentProjectId == null) {
//                        System.out.println("Skipping row " + rowIndex + " as no project info available.");
//                        continue;
//                    }
//                }
//
//                // --------------- Process Student  --------------- //
//                Optional<Student> optStudent = studentRepository.findById(studentId);
//                if (optStudent.isPresent()) {
//                    Student student = optStudent.get();
//                    if (!student.getStudentName().equals(studentName)) {
//                        System.out.println("Student info mismatch for ID " + studentId
//                                + ": Database Name = '" + student.getStudentName()
//                                + "', File Name = '" + studentName + "'. Skipping row " + rowIndex + ".");
//                        continue;
//                    }
//                } else {
//                    System.out.println("Student not found for ID " + studentId + " at row " + rowIndex + ". Skipping row.");
//                    continue;
//                }
//
//                // --------------- Process StudentProject --------------- //
//                Project project = createdProjects.get(currentProjectId);
//                if (project == null) {
//                    Optional<Project> projectOpt = projectRepository.findById(currentProjectId);
//                    if (projectOpt.isPresent()) {
//                        project = projectOpt.get();
//                    } else {
//                        System.out.println("Skipping row " + rowIndex + " as project not found for ID: " + currentProjectId);
//                        continue;
//                    }
//                }
//                StudentProject studentProject = new StudentProject();
//                studentProject.setStudent(optStudent.get());
//                studentProject.setProject(project);
//                studentProject.setStatus("Active");
//                studentProject.setStudentPjId("SP" + String.format("%03d", studentIdCounter++));
//                studentProjectRepository.save(studentProject);
//
//                // --------------- Process Advisor for ProjectInstructorRole --------------- //
//                if (!advisor.isEmpty()) {
//                    if (!isValidInstructor(advisor)) {
//                        System.out.println("Advisor name '" + advisor + "' is invalid at row " + rowIndex + ". Skipping advisor assignment.");
//                    } else {
//                        Optional<Instructor> optInstructor = instructorRepository.findByProfessorName(advisor);
//                        if (!optInstructor.isPresent()) {
//                            System.out.println("Advisor '" + advisor + "' not found in Instructor entity at row " + rowIndex + ". Skipping advisor assignment.");
//                        } else {
//                            Instructor instructor = optInstructor.get();
//                            if (instructor.getProfessorId() == null) {
//                                String newProfessorId = generateNextInstructorId();
//                                instructor.setProfessorId(newProfessorId);
//                                instructorRepository.save(instructor);
//                            }
//                            if (isInstructorAlreadyAssignedToProject(instructor.getProfessorId(), currentProjectId)) {
//                                System.out.println("Advisor '" + advisor + "' is already assigned to project " + currentProjectId + " at row " + rowIndex + ". Skipping assignment.");
//                            } else {
//                                String newInstructorRoleId = generateNextInstructorId();
//                                ProjectInstructorRole roleRecord = new ProjectInstructorRole();
//                                roleRecord.setInstructorId(newInstructorRoleId);
//                                roleRecord.setAssignDate(LocalDateTime.now());
//                                roleRecord.setRole("Advisor");
//                                roleRecord.setProjectIdRole(project);
//                                roleRecord.setInstructor(instructor);
//                                projectInstructorRoleRepository.save(roleRecord);
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (IOException e) {
//            throw new Exception("Error reading CSV file: " + e.getMessage());
//        }
//    }
//
//
//    // ฟังก์ชันในการดึงรหัส Project ล่าสุด และเพิ่มขึ้นเป็นตัวเลขต่อไป (คืนค่าเป็น int)
//    private int generateNextProjectNumber(String program, String year) {
//        String latestProjectId = projectRepository.findLatestProjectIdByProgramAndYear(program, year);
//        if (latestProjectId == null || latestProjectId.isEmpty()) {
//            return 1;
//        }
//        String[] parts = latestProjectId.split("-");
//        if (parts.length < 2) {
//            return 1;
//        }
//        try {
//            int latestNumber = Integer.parseInt(parts[1]);
//            return latestNumber + 1;
//        } catch (NumberFormatException ex) {
//            return 1;
//        }
//    }
//
//    // ฟังก์ชันในการดึงรหัส student_pj_id ล่าสุดจากฐานข้อมูล และเพิ่มขึ้นในรูปแบบที่มีหลักนำ
//    private String generateNextStudentPjId() {
//        String latestId = studentProjectRepository.findLatestStudentPjId();
//        if (latestId == null || latestId.isEmpty()) {
//            return "01";
//        }
//        String numericPart = latestId.substring(2);
//        int nextNumber = Integer.parseInt(numericPart) + 1;
//        int width = numericPart.length();
//        return String.format("%0" + width + "d", nextNumber);
//    }


    // ----------------- Function Upload Instructor Project ----------------- //
    // Process Project Advisor and Instructor Roles and upload to ProjectInstructorRole entity
    private void processProjectInstructor(MultipartFile file) throws Exception {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            int rowIndex = 0;

            // สร้าง Map เพื่อเก็บข้อมูล projectId และลิสต์ของอาจารย์ที่มี projectId เดียวกัน
            Map<String, List<String>> projectInstructorMap = new HashMap<>();

            String currentProjectId = null;

            while ((line = br.readLine()) != null) {
                rowIndex++;

                if (line.trim().isEmpty()) {
                    System.out.println("Skipping empty line at index " + rowIndex);
                    continue;  // ข้ามแถวที่ว่าง
                }

                String[] values = line.split(",");
                if (values.length < 10) {
                    System.out.println("Skipping invalid row at index " + rowIndex + ", not enough columns.");
                    continue;  // ข้ามแถวที่มีข้อมูลไม่ครบ
                }

                String projectId = values[0].trim(); // projectId (จะมีชื่ออาจารย์ในแถวนี้)
                String advisor = values[7].trim();
                String coAdvisor = values[8].trim();
                String committee = values[9].trim();
                String posterCommittee = values[10].trim();

                // ถ้าไม่พบ projectId ในแถวนี้, ใช้ projectId จากแถวแรก
                if (projectId.isEmpty() && currentProjectId != null) {
                    projectId = currentProjectId;
                } else if (!projectId.isEmpty()) {
                    currentProjectId = projectId; // เก็บ projectId จากแถวนี้
                }

                // ตรวจสอบว่า projectId ตรงกับ Project ที่มีอยู่ในฐานข้อมูลหรือไม่
                Optional<Project> existingProject = projectRepository.findById(projectId);
                if (existingProject.isEmpty()) {
                    System.out.println("Project with ID " + projectId + " not found in Project entity, skipping row...");
                    continue;  // ถ้าไม่พบ Project ให้ข้ามแถวนี้ไป
                }

                // เก็บข้อมูลอาจารย์ใน projectInstructorMap
                if (!projectInstructorMap.containsKey(projectId)) {
                    projectInstructorMap.put(projectId, new ArrayList<>());
                }

                List<String> instructorList = projectInstructorMap.get(projectId);

                // เพิ่มอาจารย์ลงใน List
                if (isValidInstructor(advisor)) {
                    instructorList.add(advisor);
                }
                if (isValidInstructor(coAdvisor)) {
                    instructorList.add(coAdvisor);
                }
                if (isValidInstructor(committee)) {
                    instructorList.add(committee);
                }
                if (isValidInstructor(posterCommittee)) {
                    instructorList.add(posterCommittee);
                }

                // ใช้ข้อมูลจาก projectInstructorMap เพื่อบันทึกข้อมูลใน ProjectInstructorRole
                for (String instructorName : instructorList) {
                    Optional<Instructor> existingInstructor = instructorRepository.findByProfessorName(instructorName);
                    if (existingInstructor.isPresent()) {
                        Instructor instructor = existingInstructor.get();

                        // ถ้าไม่มี instructorId, สร้างใหม่
                        if (instructor.getProfessorId() == null) {
                            String instructorId = generateNextInstructorId();  // ใช้ฟังก์ชันในการสร้าง instructor_id ใหม่
                            instructor.setProfessorId(instructorId); // ตั้งค่ารหัสอาจารย์ที่เพิ่งสร้างใหม่
                        }

                        // ใช้ Role ที่ตรงกับข้อมูลจากไฟล์ (Advisor, Co-Advisor, Committee, Poster-Committee)
                        String role = getRoleForInstructor(instructorName, advisor, coAdvisor, committee, posterCommittee);

                        // ตรวจสอบว่ามีการมอบหมายอาจารย์ไปยังโปรเจ็กต์นี้แล้วหรือยัง
                        if (!isInstructorAlreadyAssignedToProject(instructor.getProfessorId(), projectId)) {
                            saveInstructorRole(existingProject.get(), instructor, role);  // บันทึกข้อมูลใน ProjectInstructorRole
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new Exception("Error reading CSV file: " + e.getMessage());
        }
    }

    // ฟังก์ชันในการกำหนด Role ที่เหมาะสมสำหรับอาจารย์แต่ละท่าน
    private String getRoleForInstructor(String instructorName, String advisor, String coAdvisor, String committee, String posterCommittee) {
        if (instructorName.equals(advisor)) {
            return "Advisor";
        } else if (instructorName.equals(coAdvisor)) {
            return "Co-Advisor";
        } else if (instructorName.equals(committee)) {
            return "Committee";
        } else if (instructorName.equals(posterCommittee)) {
            return "Poster-Committee";
        }
        return "";
    }

    // ฟังก์ชันในการตรวจสอบว่าเป็นชื่ออาจารย์ที่ถูกต้องหรือไม่
    private boolean isValidInstructor(String instructor) {
        return instructor != null && !instructor.trim().isEmpty() && !instructor.equals("Aj. XXXX");
    }

    // ฟังก์ชันในการตรวจสอบว่า Instructor มีบทบาทใน projectId เดียวกันหรือยัง
    private boolean isInstructorAlreadyAssignedToProject(String professorId, String projectId) {
        Optional<ProjectInstructorRole> existingRole = projectInstructorRoleRepository.findByProjectIdRole_ProjectIdAndInstructor_ProfessorId(projectId, professorId);
        return existingRole.isPresent();
    }

    // ฟังก์ชันในการบันทึก ProjectInstructorRole
    private void saveInstructorRole(Project project, Instructor instructor, String role) {
        ProjectInstructorRole projectInstructorRole = new ProjectInstructorRole();
        projectInstructorRole.setProjectIdRole(project);
        projectInstructorRole.setRole(role);
        projectInstructorRole.setInstructor(instructor);
        projectInstructorRole.setAssignDate(LocalDateTime.now());
        projectInstructorRole.setInstructorId(generateNextInstructorId());  // สร้าง instructorId ใหม่

        // ตรวจสอบว่าอาจารย์ถูกเพิ่มไปแล้วหรือยัง
        if (!isInstructorAlreadyAssignedToProject(instructor.getProfessorId(), project.getProjectId())) {
            projectInstructorRoleRepository.save(projectInstructorRole);  // บันทึกข้อมูล
            System.out.println("Saved Instructor Role for: " + instructor + " with role: " + role);
        } else {
            System.out.println("Instructor " + instructor.getProfessorName() + " already assigned to project " + project.getProjectId());
        }
    }

    // ฟังก์ชันในการสร้าง instructor_id ใหม่
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


//    ------- โค้ดนี้น่าจะใช้ในการอัปโหลด Committee and Poster-Committee ได้ ---------
    // Process Project Advisor (เก็บเฉพาะ Advisor) และอัปโหลดลงใน ProjectInstructorRole
//    private void processProjectInstructor(MultipartFile file) throws Exception {
//        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
//            String line;
//            int rowIndex = 0;
//
//            // Map เพื่อเก็บข้อมูล projectId กับ List ของ Advisor
//            Map<String, List<String>> projectAdvisorMap = new HashMap<>();
//
//            String currentProjectId = null;
//
//            while ((line = br.readLine()) != null) {
//                rowIndex++;
//
//                if (line.trim().isEmpty()) {
//                    System.out.println("Skipping empty line at index " + rowIndex);
//                    continue;  // ข้ามแถวที่ว่าง
//                }
//
//                String[] values = line.split(",");
//                // ตรวจสอบให้มีข้อมูลอย่างน้อย 8 คอลัมน์ (ProjectId และ Advisor)
//                if (values.length < 8) {
//                    System.out.println("Skipping invalid row at index " + rowIndex + ", not enough columns.");
//                    continue;  // ข้ามแถวที่มีข้อมูลไม่ครบ
//                }
//
//                // อ่านข้อมูลจากไฟล์
//                String projectId = values[0].trim(); // projectId จากไฟล์
//                String advisor = values[7].trim();     // อ่าน Advisor จากไฟล์ (column 8, index 7)
//
//                // ถ้า projectId ว่างในแถวนี้ ให้ใช้ projectId จากแถวก่อนหน้า
//                if (projectId.isEmpty() && currentProjectId != null) {
//                    projectId = currentProjectId;
//                } else if (!projectId.isEmpty()) {
//                    currentProjectId = projectId;
//                }
//
//                // ตรวจสอบว่ามี Project ในฐานข้อมูลหรือไม่
//                Optional<Project> existingProject = projectRepository.findById(projectId);
//                if (existingProject.isEmpty()) {
//                    System.out.println("Project with ID " + projectId + " not found in Project entity, skipping row...");
//                    continue;  // ไม่พบ Project ให้ข้ามแถว
//                }
//
//                // เก็บข้อมูล Advisor ใน Map (key = projectId)
//                projectAdvisorMap.putIfAbsent(projectId, new ArrayList<>());
//                List<String> advisorList = projectAdvisorMap.get(projectId);
//                if (isValidInstructor(advisor)) {
//                    advisorList.add(advisor);
//                }
//
//                // ใช้ข้อมูล Advisor จาก Map เพื่อบันทึกลงใน ProjectInstructorRole
//                for (String advisorName : advisorList) {
//                    Optional<Instructor> existingInstructor = instructorRepository.findByProfessorName(advisorName);
//                    if (existingInstructor.isPresent()) {
//                        Instructor instructor = existingInstructor.get();
//
//                        // สร้าง instructorId ใหม่หากยังไม่มี
//                        if (instructor.getProfessorId() == null) {
//                            String instructorId = generateNextInstructorId();
//                            instructor.setProfessorId(instructorId);
//                        }
//
//                        // ตั้ง role เป็น "Advisor" เนื่องจากเราอ่านเฉพาะ Advisor
//                        String role = "Advisor";
//
//                        // ตรวจสอบว่าอาจารย์นี้ได้รับมอบหมายในโปรเจกต์นี้แล้วหรือยัง
//                        if (!isInstructorAlreadyAssignedToProject(instructor.getProfessorId(), projectId)) {
//                            saveInstructorRole(existingProject.get(), instructor, role);
//                        }
//                    }
//                }
//            }
//        } catch (IOException e) {
//            throw new Exception("Error reading CSV file: " + e.getMessage());
//        }
//    }


    // -------------------- DELETE PROJECT -------------------- //

    @Transactional
    public void deleteProjectDetails(String projectId) {
        // ตรวจสอบว่าโปรเจกต์มีอยู่หรือไม่
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found for ID: " + projectId));

//        // ลบข้อมูลอาจารย์ที่ปรึกษาที่เกี่ยวข้องกับโปรเจกต์นี้
//        List<ProjectInstructorRole> existingRoles = projectInstructorRoleRepository.findByProjectIdRole_ProjectId(projectId);
//        if (existingRoles != null && !existingRoles.isEmpty()) {
//            projectInstructorRoleRepository.deleteAll(existingRoles);  // ลบอาจารย์ที่ปรึกษาทั้งหมดที่เกี่ยวข้องกับโปรเจกต์นี้
//        }

        // ลบข้อมูลนักศึกษาที่เกี่ยวข้องกับโปรเจกต์นี้
        List<StudentProject> studentProjects = studentProjectRepository.findByProject_ProjectId(projectId);
        if (studentProjects != null && !studentProjects.isEmpty()) {
            studentProjectRepository.deleteAll(studentProjects);  // ลบข้อมูลนักศึกษาทั้งหมดที่เกี่ยวข้องกับโปรเจกต์นี้
        }

        // ลบโปรเจกต์จากฐานข้อมูล
        projectRepository.delete(project);
    }

    public void deleteAllProjects() {
        // ลบทุกโปรเจกต์ในฐานข้อมูล
        projectRepository.deleteAll();
    }


//    private void processExcel(MultipartFile file, List<Student> students, List<Project> projects, List<StudentProject> studentProjects, List<String> errorLogs) {
//        try {
//            EasyExcel.read(file.getInputStream(), ExcelDataDTO.class, new ReadListener<ExcelDataDTO>() {
//                @Override
//                public void invoke(ExcelDataDTO data, AnalysisContext context) {
//                    System.out.println("Excel Data Read: " + data);
//
//                    // ตรวจสอบว่า studentId ไม่เป็นค่าว่าง
//                    if (data == null || data.getStudentId() == null || data.getStudentId().isEmpty()) {
//                        errorLogs.add("Missing Student ID: " + data);
//                        return;
//                    }
//
//                    // จัดเตรียมข้อมูลแถว (row) สำหรับ mapProjectToEntities
//                    String[] rowData = {
//                            data.getProjectId(), data.getProjectTitle(), data.getProjectDescription(),
//                            data.getAdvisor(), data.getCommittee(), data.getPosterCommittee(),
//                            data.getStudentId(), data.getStudentName(), data.getProgram(),
//                            data.getSection(), data.getTrack()
//                    };
//
//                    System.out.println("Row Data: " + Arrays.toString(rowData));
//                    // ส่งข้อมูลไปยังฟังก์ชัน mapProjectToEntities
//                    mapProjectToEntities(rowData, students, projects, studentProjects, errorLogs);
//                }
//
//                @Override
//                public void doAfterAllAnalysed(AnalysisContext context) {
//                    // สามารถใช้เพื่อทำการ cleanup หรือการประมวลผลหลังจากที่อ่านข้อมูลเสร็จ
//                }
//            }).sheet().doRead();
//        } catch (Exception e) {
//            System.out.println("Error processing Excel: " + e.getMessage());
//            errorLogs.add("Error processing Excel: " + e.getMessage());
//        }
//    }

    // ----------------- Function Upload Project Details (บันทึกลง DB) ----------------- //
//    public List<String> processProjectAndStudent(MultipartFile file) throws Exception {
//        List<String> warnings = new ArrayList<>();
//        try (BufferedReader br = new BufferedReader(
//                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
//            String line;
//            int rowIndex = 0;
//            int currentYear = LocalDate.now().getYear();
//
//            Map<String, Integer> projectNumberCounters = new HashMap<>();
//            Map<String, Project> createdProjects = new HashMap<>();
//            String currentProjectId = null;
//
//            int studentIdCounter = Integer.parseInt(generateNextStudentPjId());
//
//            while ((line = br.readLine()) != null) {
//                rowIndex++;
//                if (rowIndex < 9) continue;
//                if (line.trim().isEmpty()) continue;
//
//                // ใช้ split ด้วย limit -1 เพื่อเก็บ trailing empty columns
//                String[] values = line.split(",", -1);
//                // ถ้าจำนวนคอลัมน์ยังน้อยกว่า 8 ให้เติมค่าว่างเข้าไป
//                if (values.length < 8) {
//                    String[] fullValues = new String[8];
//                    for (int i = 0; i < 8; i++) {
//                        if (i < values.length) {
//                            fullValues[i] = values[i].trim();
//                        } else {
//                            fullValues[i] = "";
//                        }
//                    }
//                    values = fullValues;
//                }
//
//                String fileProjectId = values[0].trim();
//                String projectTitle = values[1].trim();
//                String projectDescription = values[2].trim();
//                String projectCategory = values[3].trim();
//                String studentId = values[4].trim();
//                String studentName = values[5].trim();
//                String program = values[6].trim();
//                String advisor = values[7].trim();
//
//                // เมื่อเจอแถวที่มีข้อมูลโปรเจกต์ใหม่ (fileProjectId มีค่า)
//                if (!fileProjectId.isEmpty()) {
//                    Optional<Project> existingProjectOpt = projectRepository.findByProjectTitleAndProjectDescription(projectTitle, projectDescription);
//                    if (existingProjectOpt.isPresent()) {
//                        System.out.println("Project already exists for title '" + projectTitle
//                                + "' and description '" + projectDescription + "' at row " + rowIndex + ". Skipping project insertion.");
//                        warnings.add("Project '" + projectTitle + "' already exists.");
//                        currentProjectId = existingProjectOpt.get().getProjectId();
//                    } else {
//                        String key = program + "_" + currentYear;
//                        int projectNumberCounter = projectNumberCounters.containsKey(key)
//                                ? projectNumberCounters.get(key) + 1
//                                : generateNextProjectNumber(program, String.valueOf(currentYear));
//                        projectNumberCounters.put(key, projectNumberCounter);
//
//                        String newProjectId = program + " SP" + currentYear + "-" + String.format("%02d", projectNumberCounter);
//                        Project project = new Project();
//                        project.setProjectId(newProjectId);
//                        project.setProgram(program);
//                        project.setSemester(String.valueOf(currentYear));
//                        project.setProjectTitle(projectTitle);
//                        project.setProjectCategory(projectCategory);
//                        project.setProjectDescription(projectDescription);
//                        project.setRecordedOn(LocalDateTime.now());
//                        project.setEditedOn(LocalDateTime.now());
//
//                        projectRepository.save(project);
//                        createdProjects.put(newProjectId, project);
//                        currentProjectId = newProjectId;
//                    }
//                } else {
//                    // ไม่มี fileProjectId -> ใช้โปรเจกต์ล่าสุด
//                    if (currentProjectId == null) {
//                        String msg = "Skipping row " + rowIndex + " as no project info available.";
//                        System.out.println(msg);
//                        warnings.add("Row " + rowIndex + ": No project information available.");
//                        continue;
//                    }
//                }
//
//                // ตรวจสอบว่านักศึกษาสำหรับโปรเจกต์นี้มีอยู่แล้วหรือไม่
//                boolean studentProjectExists = studentProjectRepository.existsByProject_ProjectIdAndStudent_StudentId(currentProjectId, studentId);
//                if (studentProjectExists) {
//                    String msg = "StudentProject already exists for project " + currentProjectId + " and student " + studentId
//                            + " at row " + rowIndex + ". Skipping student-project insertion.";
//                    System.out.println(msg);
//                    warnings.add("Student '" + studentId + "' already exists in Project '" + currentProjectId + "'.");
//                    continue;
//                }
//
//                // ตรวจสอบข้อมูลนักศึกษา
//                Optional<Student> optStudent = studentRepository.findById(studentId);
//                if (optStudent.isPresent()) {
//                    Student student = optStudent.get();
//                    if (!student.getStudentName().equals(studentName)) {
//                        String msg = "Student info mismatch for ID " + studentId
//                                + ": Database Name = '" + student.getStudentName() + "', File Name = '" + studentName + "'. Skipping row " + rowIndex + ".";
//                        System.out.println(msg);
//                        warnings.add("Student information mismatch for ID '" + studentId + "'.");
//                        continue;
//                    }
//                } else {
//                    String msg = "Student not found for ID " + studentId + " at row " + rowIndex + ". Skipping row.";
//                    System.out.println(msg);
//                    warnings.add("Student '" + studentId + "' not found.");
//                    continue;
//                }
//
//                Project project = createdProjects.get(currentProjectId);
//                if (project == null) {
//                    Optional<Project> projectOpt = projectRepository.findById(currentProjectId);
//                    if (projectOpt.isPresent()) {
//                        project = projectOpt.get();
//                    } else {
//                        String msg = "Skipping row " + rowIndex + " as project not found for ID: " + currentProjectId;
//                        System.out.println(msg);
//                        warnings.add("Project not found for ID '" + currentProjectId + "'.");
//                        continue;
//                    }
//                }
//
//                StudentProject studentProject = new StudentProject();
//                studentProject.setStudent(optStudent.get());
//                studentProject.setProject(project);
//                studentProject.setStatus("Active");
//                studentProject.setStudentPjId("SP" + String.format("%03d", studentIdCounter++));
//                studentProjectRepository.save(studentProject);
//
//                if (!advisor.isEmpty()) {
//                    if (!isValidInstructor(advisor)) {
//                        String msg = "Advisor name '" + advisor + "' is invalid at row " + rowIndex + ". Skipping advisor assignment.";
//                        System.out.println(msg);
//                        warnings.add("Invalid advisor '" + advisor + "' for Project '" + currentProjectId + "'.");
//                    } else {
//                        Optional<Instructor> optInstructor = instructorRepository.findByProfessorName(advisor);
//                        if (!optInstructor.isPresent()) {
//                            String msg = "Advisor '" + advisor + "' not found in Instructor entity at row " + rowIndex + ". Skipping advisor assignment.";
//                            System.out.println(msg);
//                            warnings.add("Advisor '" + advisor + "' not found for Project '" + currentProjectId + "'.");
//                        } else {
//                            Instructor instructor = optInstructor.get();
//                            if (instructor.getProfessorId() == null) {
//                                String newProfessorId = generateNextInstructorId();
//                                instructor.setProfessorId(newProfessorId);
//                                instructorRepository.save(instructor);
//                            }
//                            boolean instructorAlreadyAssigned = projectInstructorRoleRepository
//                                    .existsByProjectIdRole_ProjectIdAndInstructor_ProfessorId(currentProjectId, instructor.getProfessorId());
//                            if (instructorAlreadyAssigned) {
//                                String msg = "Advisor '" + advisor + "' is already assigned to project " + currentProjectId
//                                        + " at row " + rowIndex + ". Skipping assignment.";
//                                System.out.println(msg);
//                                warnings.add("Advisor '" + advisor + "' already exists in Project '" + currentProjectId + "'.");
//                            } else {
//                                String newInstructorRoleId = generateNextInstructorId();
//                                ProjectInstructorRole roleRecord = new ProjectInstructorRole();
//                                roleRecord.setInstructorId(newInstructorRoleId);
//                                roleRecord.setAssignDate(LocalDateTime.now());
//                                roleRecord.setRole("Advisor");
//                                roleRecord.setProjectIdRole(project);
//                                roleRecord.setInstructor(instructor);
//                                projectInstructorRoleRepository.save(roleRecord);
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (IOException e) {
//            throw new Exception("Error reading CSV file: " + e.getMessage());
//        }
//        return warnings;
//    }



    /**
     * 1) อ่านไฟล์ทั้งหมด สร้างโครงสร้าง in‑memory ของแต่ละโปรเจกต์
     * 2) ตรวจสอบใน DB (StudentProject, ProjectInstructorRole) ว่าตรงกับไฟล์หรือไม่
     * 3) คืน Map<projectId, List<discrepancyMessages>> (ว่าง = ผ่าน)
     */
    public Map<String, List<String>> validateProjectAndStudent(MultipartFile file) throws IOException {
        Map<String, FileProjectData> fileData = parseFile(file);
        Map<String, List<String>> errors = new LinkedHashMap<>();

        for (Map.Entry<String, FileProjectData> entry : fileData.entrySet()) {
            String projectId = entry.getKey();
            FileProjectData fp = entry.getValue();
            List<String> projectErrors = new ArrayList<>();

            if (projectRepository.existsById(projectId)) {
                // — ตรวจ studentProject
                List<StudentProject> dbSP = studentProjectRepository
                        .findByProject_ProjectId(projectId);
                Set<String> fileStudents = fp.students.stream()
                        .map(s -> s.id + "|" + s.name)
                        .collect(Collectors.toSet());
                Set<String> dbStudents = dbSP.stream()
                        .map(sp -> sp.getStudent().getStudentId() + "|" + sp.getStudent().getStudentName())
                        .collect(Collectors.toSet());

                for (String fs : fileStudents) {
                    if (!dbStudents.contains(fs)) {
                        projectErrors.add("Missing in DB StudentProject: " + fs);
                    }
                }
                for (String ds : dbStudents) {
                    if (!fileStudents.contains(ds)) {
                        projectErrors.add("Extra in DB StudentProject: " + ds);
                    }
                }

                // — ตรวจ ProjectInstructorRole
                List<ProjectInstructorRole> dbIR = projectInstructorRoleRepository
                        .findByProjectIdRole_ProjectId(projectId);
                Set<String> fileInstructors = fp.advisors.stream()
                        .filter(a -> !a.isEmpty())
                        .collect(Collectors.toSet());
                Set<String> dbInstructors = dbIR.stream()
                        .map(ir -> ir.getInstructor().getProfessorName())
                        .collect(Collectors.toSet());

                for (String fi : fileInstructors) {
                    if (!dbInstructors.contains(fi)) {
                        projectErrors.add("Missing in DB Advisor: " + fi);
                    }
                }
                for (String di : dbInstructors) {
                    if (!fileInstructors.contains(di)) {
                        projectErrors.add("Extra in DB Advisor: " + di);
                    }
                }
            }
            if (!projectErrors.isEmpty()) {
                errors.put(projectId, projectErrors);
            }
        }

        return errors;
    }

    /**
     * ถ้า validation ผ่าน (หรือ front-end ยืนยันมา) จึงเขียนลง DB
     */
    @Transactional
    public List<String> processProjectAndStudent(MultipartFile file) throws Exception {
        Map<String, FileProjectData> fileData = parseFile(file);
        List<String> warnings = new ArrayList<>();
        int currentYear = LocalDate.now().getYear();
        Map<String, Integer> projectCounters = new HashMap<>();
        int studentPjCounter = Integer.parseInt(generateNextStudentPjId());

        for (FileProjectData fp : fileData.values()) {
            // 1) หาใน DB จาก title+description (หรือ projectId เดิม)
            Optional<Project> optProj = projectRepository
                    .findByProjectTitleAndProjectDescription(fp.title, fp.description);

            Project project;
            if (optProj.isPresent()) {
                // — ถ้ามีอยู่แล้ว: อัปเดตฟิลด์ที่เปลี่ยนแปลง
                project = optProj.get();
                project.setProgram(fp.program);
                project.setSemester(String.valueOf(currentYear));
                project.setProjectCategory(fp.category);
                project.setProjectDescription(fp.description);
                project.setEditedOn(LocalDateTime.now());
                projectRepository.save(project);

                // — ลบข้อมูลเก่า เพื่อเตรียมแทนที่ใหม่
                studentProjectRepository.deleteByProject_ProjectId(project.getProjectId());
                projectInstructorRoleRepository.deleteByProjectIdRole_ProjectId(project.getProjectId());
            } else {
                // — ถ้าไม่มี ให้สร้างใหม่ตามเดิม
                String key = fp.program + "_" + currentYear;
                int nextNum = projectCounters.getOrDefault(key,
                        generateNextProjectNumber(fp.program, String.valueOf(currentYear))
                );
                projectCounters.put(key, nextNum);

                String newId = fp.program + " SP" + currentYear
                        + "-" + String.format("%02d", nextNum);

                project = new Project();
                project.setProjectId(newId);
                project.setProgram(fp.program);
                project.setSemester(String.valueOf(currentYear));
                project.setProjectTitle(fp.title);
                project.setProjectCategory(fp.category);
                project.setProjectDescription(fp.description);
                project.setRecordedOn(LocalDateTime.now());
                project.setEditedOn(LocalDateTime.now());
                projectRepository.save(project);
            }

            // 2) สร้าง StudentProject ใหม่ (แทนที่ข้อมูลเก่า)
            for (FileStudent fs : fp.students) {
                if (fs.id == null || fs.id.isBlank()) continue;

                // เช็คว่ามีนักศึกษาในระบบ และชื่อตรงกัน
                Student student = studentRepository.findById(fs.id)
                        .orElseThrow(() -> new IllegalStateException("Student not found: " + fs.id));
                if (!student.getStudentName().equals(fs.name)) {
                    throw new IllegalStateException(
                            "Name mismatch for " + fs.id + ": DB=" +
                                    student.getStudentName() + ", File=" + fs.name);
                }

                // สร้าง record ใหม่
                StudentProject sp = new StudentProject();
                sp.setStudent(student);
                sp.setProject(project);
                sp.setStatus("Active");
                sp.setStudentPjId("SP" + String.format("%03d", studentPjCounter++));
                studentProjectRepository.save(sp);
            }

            // 3) สร้าง ProjectInstructorRole ใหม่ (แทนที่ข้อมูลเก่า)
            for (String advName : fp.advisors) {
                if (advName == null || advName.isBlank()) continue;
                if (!isValidInstructor(advName)) {
                    throw new IllegalStateException("Invalid advisor format: " + advName);
                }

                Instructor instr = instructorRepository
                        .findByProfessorName(advName)
                        .orElseThrow(() -> new IllegalStateException("Advisor not found: " + advName));

                if (instr.getProfessorId() == null) {
                    instr.setProfessorId(generateNextInstructorId());
                    instructorRepository.save(instr);
                }

                ProjectInstructorRole pir = new ProjectInstructorRole();
                pir.setInstructorId(generateNextInstructorId());
                pir.setAssignDate(LocalDateTime.now());
                pir.setRole("Advisor");
                pir.setProjectIdRole(project);
                pir.setInstructor(instr);
                projectInstructorRoleRepository.save(pir);
            }
        }

        return warnings;
    }

    // —— HELPER TO PARSE FILE INTO IN‑MEMORY STRUCTURES ——

    private Map<String, FileProjectData> parseFile(MultipartFile file) throws IOException {
        Map<String, FileProjectData> map = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            int row = 0;
            String currentProjId = null;

            while ((line = br.readLine()) != null) {
                row++;
                if (row < 9 || line.trim().isEmpty()) continue;

                String[] cols = line.split(",", -1);
                // pad to length 8
                if (cols.length < 8) {
                    String[] tmp = new String[8];
                    System.arraycopy(cols, 0, tmp, 0, cols.length);
                    for (int i = cols.length; i < 8; i++) tmp[i] = "";
                    cols = tmp;
                }

                String fid      = cols[0].trim();
                String title    = cols[1].trim();
                String desc     = cols[2].trim();
                String category = cols[3].trim();
                String sid      = cols[4].trim();
                String sname    = cols[5].trim();
                String prog     = cols[6].trim();
                String adv      = cols[7].trim();

                if (!fid.isEmpty()) {
                    currentProjId = fid;
                    map.putIfAbsent(currentProjId, new FileProjectData(
                            currentProjId, title, desc, category, prog
                    ));
                }
                if (currentProjId == null) {
                    throw new IllegalStateException("Row " + row + ": no projectId");
                }

                FileProjectData fp = map.get(currentProjId);
                if (!sid.isEmpty()) {
                    fp.students.add(new FileStudent(sid, sname));
                }
                if (!adv.isEmpty() && !fp.advisors.contains(adv)) {
                    fp.advisors.add(adv);
                }
            }
        }
        return map;
    }

    // —— in‑memory DTO’s ——
    private static class FileProjectData {
        String projectId, title, description, category, program;
        List<FileStudent> students   = new ArrayList<>();
        List<String>      advisors   = new ArrayList<>();

        FileProjectData(String projectId, String title, String desc,
                        String cat, String prog) {
            this.projectId = projectId;
            this.title     = title;
            this.description = desc;
            this.category  = cat;
            this.program   = prog;
        }
    }

    private static class FileStudent {
        String id, name;

        FileStudent(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public String generateNextProjectId(String program, String year) {
        int nextNum = generateNextProjectNumber(program, year);
        return String.format("%s SP%s-%02d", program, year, nextNum);
    }














    // ฟังก์ชันในการดึงรหัส Project ล่าสุด และเพิ่มขึ้นเป็นตัวเลขต่อไป (คืนค่าเป็น int)
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

    // ฟังก์ชันในการดึงรหัส student_pj_id ล่าสุดจากฐานข้อมูล และเพิ่มขึ้นในรูปแบบที่มีหลักนำ
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


    public List<String> processProjectCommittee(MultipartFile file) throws Exception {
        List<String> warnings = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            int rowIndex = 0;
            String currentProjectId = null;  // projectId สำหรับผูก Committee

            // อ่านไฟล์ CSV ทีละบรรทัด (สมมติ header อยู่ที่แถว 1-9)
            while ((line = br.readLine()) != null) {
                rowIndex++;
                if (rowIndex < 10) continue;  // ข้าม header
                if (line.trim().isEmpty()) continue;

                // ใช้ regex split เพื่อแยกเฉพาะ comma ที่ไม่ได้อยู่ภายใน double quotes
                String pattern = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
                String[] values = line.split(pattern, -1);

                // หากจำนวนคอลัมน์ที่ได้ไม่ครบ 10 ให้เติมคอลัมน์ด้วยค่า ""
                if (values.length < 10) {
                    String[] fullValues = new String[10];
                    for (int i = 0; i < 10; i++) {
                        if (i < values.length) {
                            fullValues[i] = values[i].trim();
                        } else {
                            fullValues[i] = "";
                        }
                    }
                    values = fullValues;
                }

                // ดึงข้อมูลจาก CSV ตามตำแหน่งที่คาดหวัง
                String fileProjectId = values[0].trim();
                String committeeName = values[9].trim();

                // --------------------- จัดการ Project ---------------------
                if (!fileProjectId.isEmpty()) {
                    Optional<Project> projectOpt = projectRepository.findById(fileProjectId);
                    if (projectOpt.isPresent()) {
                        currentProjectId = projectOpt.get().getProjectId();
                    } else {
                        System.out.println("Project with ID '" + fileProjectId + "' not found at row " + rowIndex + ".");
                        warnings.add("Row " + rowIndex + ": Project with ID '" + fileProjectId + "' not found.");
                        continue;
                    }
                } else {
                    if (currentProjectId == null) {
                        System.out.println("Row " + rowIndex + " has no project ID provided (no previous project).");
//                        warnings.add("Row " + rowIndex + ": No project ID provided (no previous project).");
                        continue;
                    }
                    // ใช้ currentProjectId เดิม
                }

                // --------------------- จัดการ Committee ---------------------
                if (committeeName.isEmpty()) {
                    System.out.println("No committee data provided at row " + rowIndex + ".");
//                    warnings.add("Row " + rowIndex + ": No committee data provided.");
                    continue;
                } else {
                    if (!isValidInstructor(committeeName)) {
                        System.out.println("Committee name '" + committeeName + "' is invalid at row " + rowIndex + ". Skipping committee assignment.");
                        warnings.add("Row " + rowIndex + ": Invalid committee '" + committeeName + "' for Project '" + currentProjectId + "'.");
                        continue;
                    } else {
                        Optional<Instructor> optInstructor = instructorRepository.findByProfessorName(committeeName);
                        if (!optInstructor.isPresent()) {
                            System.out.println("Committee '" + committeeName + "' not found in Instructor entity at row " + rowIndex + ". Skipping committee assignment.");
                            warnings.add("Row " + rowIndex + ": Committee '" + committeeName + "' not found for Project '" + currentProjectId + "'.");
                            continue;
                        }
                        Instructor instructor = optInstructor.get();

                        if (instructor.getProfessorId() == null) {
                            String newProfessorId = generateNextInstructorId();
                            instructor.setProfessorId(newProfessorId);
                            instructorRepository.save(instructor);
                        }

                        boolean committeeAlreadyAssigned = projectInstructorRoleRepository
                                .existsByProjectIdRole_ProjectIdAndInstructor_ProfessorIdAndRole(
                                        currentProjectId, instructor.getProfessorId(), "Committee");
                        if (committeeAlreadyAssigned) {
                            System.out.println("Committee '" + committeeName + "' is already assigned to project " + currentProjectId
                                    + " at row " + rowIndex + ". Skipping assignment.");
                            warnings.add("Row " + rowIndex + ": Committee '" + committeeName + "' already exists in Project '" + currentProjectId + "'.");
                        } else {
                            String newCommitteeRoleId = generateNextInstructorId();
                            ProjectInstructorRole roleRecord = new ProjectInstructorRole();
                            roleRecord.setInstructorId(newCommitteeRoleId);
                            roleRecord.setAssignDate(LocalDateTime.now());
                            roleRecord.setRole("Committee");

                            Optional<Project> projOpt = projectRepository.findById(currentProjectId);
                            if (!projOpt.isPresent()) {
                                System.out.println("Project not found for ID '" + currentProjectId + "' at row " + rowIndex + ".");
                                warnings.add("Row " + rowIndex + ": Project not found for ID '" + currentProjectId + "'.");
                                continue;
                            }
                            Project project = projOpt.get();
                            roleRecord.setProjectIdRole(project);
                            roleRecord.setInstructor(instructor);

                            projectInstructorRoleRepository.save(roleRecord);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new Exception("Error reading CSV file: " + e.getMessage());
        }
        return warnings;
    }


    public List<String> processProjectPosterCommittee(MultipartFile file) throws Exception {
        List<String> warnings = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            int rowIndex = 0;
            String currentProjectId = null;  // projectId สำหรับผูก posterCommittee

            // อ่านไฟล์ CSV ทีละบรรทัด (สมมติ header อยู่ที่แถว 1-9)
            while ((line = br.readLine()) != null) {
                rowIndex++;
                if (rowIndex < 10) continue;  // ข้าม header
                if (line.trim().isEmpty()) continue;

                // ใช้ regex split เพื่อแยกเฉพาะ comma ที่ไม่ได้อยู่ภายใน double quotes
                String pattern = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
                String[] values = line.split(pattern, -1);

                // ต้องการขั้นต่ำ 11 คอลัมน์ (index 0..10) เพราะ posterCommittee อยู่คอลัมน์ที่ 10
                if (values.length < 11) {
                    String[] fullValues = new String[11];
                    for (int i = 0; i < 11; i++) {
                        if (i < values.length) {
                            fullValues[i] = values[i].trim();
                        } else {
                            fullValues[i] = "";
                        }
                    }
                    values = fullValues;
                }

                // ดึงข้อมูลจาก CSV ตามตำแหน่งที่คาดหวัง
                String fileProjectId = values[0].trim();
                String posterCommittee = values[10].trim(); // เปลี่ยนจากคอลัมน์ที่ 9 เป็นคอลัมน์ที่ 10

                // --------------------- จัดการ Project ---------------------
                if (!fileProjectId.isEmpty()) {
                    Optional<Project> projectOpt = projectRepository.findById(fileProjectId);
                    if (projectOpt.isPresent()) {
                        currentProjectId = projectOpt.get().getProjectId();
                    } else {
                        System.out.println("Project with ID '" + fileProjectId + "' not found at row " + rowIndex + ".");
                        warnings.add("Row " + rowIndex + ": Project with ID '" + fileProjectId + "' not found.");
                        continue;
                    }
                } else {
                    if (currentProjectId == null) {
                        System.out.println("Row " + rowIndex + " has no project ID provided (no previous project).");
                        continue;
                    }
                    // ใช้ currentProjectId เดิม
                }

                // --------------------- จัดการ posterCommittee ---------------------
                if (posterCommittee.isEmpty()) {
                    System.out.println("No posterCommittee data provided at row " + rowIndex + ".");
                    continue;
                } else {
                    if (!isValidInstructor(posterCommittee)) {
                        System.out.println("posterCommittee name '" + posterCommittee + "' is invalid at row " + rowIndex + ". Skipping posterCommittee assignment.");
                        warnings.add("Row " + rowIndex + ": Invalid posterCommittee '" + posterCommittee + "' for Project '" + currentProjectId + "'.");
                        continue;
                    } else {
                        Optional<Instructor> optInstructor = instructorRepository.findByProfessorName(posterCommittee);
                        if (!optInstructor.isPresent()) {
                            System.out.println("posterCommittee '" + posterCommittee + "' not found in Instructor entity at row " + rowIndex + ". Skipping posterCommittee assignment.");
                            warnings.add("Row " + rowIndex + ": posterCommittee '" + posterCommittee + "' not found for Project '" + currentProjectId + "'.");
                            continue;
                        }
                        Instructor instructor = optInstructor.get();

                        if (instructor.getProfessorId() == null) {
                            String newProfessorId = generateNextInstructorId();
                            instructor.setProfessorId(newProfessorId);
                            instructorRepository.save(instructor);
                        }

                        boolean committeeAlreadyAssigned = projectInstructorRoleRepository
                                .existsByProjectIdRole_ProjectIdAndInstructor_ProfessorIdAndRole(
                                        currentProjectId, instructor.getProfessorId(), "posterCommittee");
                        if (committeeAlreadyAssigned) {
                            System.out.println("posterCommittee '" + posterCommittee + "' is already assigned to project " + currentProjectId
                                    + " at row " + rowIndex + ". Skipping assignment.");
                            warnings.add("Row " + rowIndex + ": posterCommittee '" + posterCommittee + "' already exists in Project '" + currentProjectId + "'.");
                        } else {
                            String newCommitteeRoleId = generateNextInstructorId();
                            ProjectInstructorRole roleRecord = new ProjectInstructorRole();
                            roleRecord.setInstructorId(newCommitteeRoleId);
                            roleRecord.setAssignDate(LocalDateTime.now());
                            roleRecord.setRole("Poster-Committee");

                            Optional<Project> projOpt = projectRepository.findById(currentProjectId);
                            if (!projOpt.isPresent()) {
                                System.out.println("Project not found for ID '" + currentProjectId + "' at row " + rowIndex + ".");
                                warnings.add("Row " + rowIndex + ": Project not found for ID '" + currentProjectId + "'.");
                                continue;
                            }
                            Project project = projOpt.get();
                            roleRecord.setProjectIdRole(project);
                            roleRecord.setInstructor(instructor);

                            projectInstructorRoleRepository.save(roleRecord);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new Exception("Error reading CSV file: " + e.getMessage());
        }
        return warnings;
    }




}