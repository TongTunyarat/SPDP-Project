package com.example.project.service.ProjectManagement;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.example.project.DTO.projectManagement.ProfessorRoleDTO;
import com.example.project.DTO.projectManagement.ProjectDetailsResponseDTO;
import com.example.project.DTO.projectManagement.StudentProjectDTO;
import com.example.project.entity.*;
import com.example.project.repository.*;
import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.example.project.DTO.projectManagement.ExcelDataDTO;

import java.io.BufferedReader;
import java.io.IOException;
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
    public void processCsvFile(MultipartFile file, String uploadType) throws Exception {
        switch (uploadType) {
            case "projectDetails":
                processProjectDetails(file);
                break;
            case "projectStudent":
                processProjectStudent(file);
                break;
            case "instructor":
                processProjectInstructor(file);
                break;
            default:
                throw new IllegalArgumentException("Invalid upload type selected");
        }
    }


    // ----------------- Function Upload Project Details ----------------- //
    private void processProjectDetails(MultipartFile file) throws Exception {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            int rowIndex = 0;

            int currentYear = LocalDate.now().getYear();

            while ((line = br.readLine()) != null) {
                rowIndex++;
                if (rowIndex < 9) continue;

                String[] values = line.split(",");
                if (values.length < 5) continue;

                String projectId = values[0].trim();
                String projectTitle = values[1].trim();
                String projectDescription = values[2].trim();

                // ตรวจสอบว่า projectId มีค่า ถ้าไม่มีจะข้ามแถวนี้ไป
                if (projectId.isEmpty()) {
                    continue;  // ถ้าไม่มี projectId ข้ามแถวนี้ไป
                }

                // ใช้ค่าจาก projectId เพื่อดึง "program" ซึ่งคือส่วนแรกของ projectId ก่อน SP
                String program = projectId.split(" ")[0]; // แยกค่าโดยช่องว่างและใช้ส่วนแรก

                Project project = new Project(projectId, program, String.valueOf(currentYear), projectTitle, "Develop", projectDescription, LocalDateTime.now(), LocalDateTime.now(), null, null);
                projectRepository.save(project);
            }
        } catch (IOException e) {
            throw new Exception("Error reading CSV file: " + e.getMessage());
        }
    }


    // ----------------- Function Upload Project Student ----------------- //
    private void processProjectStudent(MultipartFile file) throws Exception {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            int rowIndex = 0;

            // สร้าง Map เพื่อเก็บข้อมูล projectId และลิสต์ของนักศึกษาที่มี projectId เดียวกัน
            Map<String, List<StudentProject>> projectStudentMap = new HashMap<>();

            // ตัวแปรเพื่อเก็บข้อมูลของนักศึกษาคนแรกในกลุ่ม
            String currentProjectId = null;
            String currentProjectTitle = null;
            String currentProjectDescription = null;

            // ใช้ฟังก์ชันในการดึงค่า student_pj_id ล่าสุดจากฐานข้อมูล
            int studentIdCounter = generateNextStudentPjId();  // เรียกฟังก์ชันเพื่อดึงค่ารหัสใหม่

            while ((line = br.readLine()) != null) {
                rowIndex++;

                // ตรวจสอบว่าแถวมีข้อมูลครบถ้วนหรือไม่
                if (line.trim().isEmpty()) continue;

                String[] values = line.split(",");
                if (values.length < 10) continue;  // ข้ามแถวที่มีข้อมูลไม่ครบ

                String studentId = values[3].trim();  // student_id
                String studentName = values[4].trim();
                String section = values[6].trim();
                String track = values[7].trim();
                String projectId = values[0].trim(); // projectId
                String projectTitle = values[1].trim(); // projectTitle
                String projectDescription = values[2].trim(); // projectDescription

                // ข้ามแถวที่ไม่มี studentId หรือ studentName
                if (studentId.isEmpty() || studentName.isEmpty()) continue;

                Optional<Student> existingStudent = studentRepository.findById(studentId);
                if (!existingStudent.isPresent()) {
                    continue;  // ถ้าไม่พบ Student ให้ข้ามแถวนี้ไป
                }

                // ตรวจสอบว่า projectId, projectTitle, projectDescription เป็นค่าว่าง (สำหรับนักศึกษาคนที่ 2 และ 3), ใช้ค่าจากนักศึกษาคนแรก
                if (projectId.isEmpty()) {
                    projectId = currentProjectId; // ใช้ projectId จากคนแรก
                } else {
                    currentProjectId = projectId; // เก็บ projectId จากคนแรก
                }

                if (projectTitle.isEmpty()) {
                    projectTitle = currentProjectTitle; // ใช้ projectTitle จากคนแรก
                } else {
                    currentProjectTitle = projectTitle; // เก็บ projectTitle จากคนแรก
                }

                if (projectDescription.isEmpty()) {
                    projectDescription = currentProjectDescription; // ใช้ projectDescription จากคนแรก
                } else {
                    currentProjectDescription = projectDescription; // เก็บ projectDescription จากคนแรก
                }

                // ตรวจสอบว่า projectId มีค่าหรือไม่ก่อนบันทึกลงฐานข้อมูล
                if (projectId == null || projectId.isEmpty()) {
                    System.out.println("Skipping row due to missing projectId at row index " + rowIndex);
                    continue; // ถ้าไม่มี projectId ให้ข้ามแถวนี้ไป
                }

                // ค้นหา Project ในฐานข้อมูล
                Optional<Project> existingProject = projectRepository.findById(projectId);
                if (!existingProject.isPresent()) {
                    System.out.println("Project with ID " + projectId + " not found in Project entity, skipping row...");
                    continue;  // ถ้าไม่พบ Project ให้ข้ามแถวนี้ไป
                }

                // สร้าง StudentProject object
                StudentProject studentProject = new StudentProject();
                studentProject.setStudent(existingStudent.get());
                studentProject.setProject(existingProject.get());
                studentProject.setStatus("Active");

                // กำหนด student_pj_id ใหม่
                String newStudentPjId = "SP" + String.format("%03d", studentIdCounter++);  // สร้างรหัสใหม่
                studentProject.setStudentPjId(newStudentPjId);  // ตั้งค่ารหัสใหม่นี้

                // ตรวจสอบว่า projectId นี้มีนักศึกษากี่คนใน Map
                if (!projectStudentMap.containsKey(projectId)) {
                    projectStudentMap.put(projectId, new ArrayList<>());
                }

                List<StudentProject> studentList = projectStudentMap.get(projectId);

                // ถ้ามี 3 คนแล้ว, ให้ไปเก็บใน projectId ใหม่
                if (studentList.size() < 3) {
                    studentList.add(studentProject);
                } else {
                    // ถ้ามีแล้ว 3 คน ให้ย้ายไปเก็บใน projectId ใหม่
                    String nextProjectId = String.valueOf(generateNextStudentPjId());  // ใช้ generateNextStudentPjId สำหรับ projectId ใหม่
                    projectStudentMap.put(nextProjectId, new ArrayList<>());
                    projectStudentMap.get(nextProjectId).add(studentProject);
                }
            }

            // บันทึกข้อมูลทั้งหมดในฐานข้อมูล
            if (!projectStudentMap.isEmpty()) {
                for (Map.Entry<String, List<StudentProject>> entry : projectStudentMap.entrySet()) {
                    for (StudentProject studentProject : entry.getValue()) {
                        studentProjectRepository.save(studentProject);
                    }
                }
            }

        } catch (IOException e) {
            throw new Exception("Error reading CSV file: " + e.getMessage());
        }
    }

    // ฟังก์ชันในการดึงรหัส student_pj_id ล่าสุดจากฐานข้อมูล
    private int generateNextStudentPjId() {
        String latestId = studentProjectRepository.findLatestStudentPjId();

        // ถ้าไม่พบรหัสล่าสุด (กรณีฐานข้อมูลยังว่างเปล่า)
        if (latestId == null) {
            return 61;  // เริ่มต้นที่ SP061
        }

        // เอารหัสล่าสุดออกมาจาก SPxxxx และเพิ่ม 1
        String numericPart = latestId.substring(2);  // เอาส่วนเลขออกจาก SPxxxx
        return Integer.parseInt(numericPart) + 1;  // ส่งกลับค่ารหัสใหม่
    }


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
                String advisor = values[8].trim();
                String coAdvisor = values[9].trim();
                String committee = values[10].trim();
                String posterCommittee = values[11].trim();

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

}