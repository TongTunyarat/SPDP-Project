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


    // ----------------- Value from Dropdown ----------------- //
    // Main method to process CSV file
    public void processCsvFile(MultipartFile file, String uploadType) throws Exception {
        switch (uploadType) {
            case "projectDetails":
                processProjectDetails(file);
                break;
//            case "projectStudent":
//                processProjectStudent(file);
//                break;
            case "instructor":
                processProjectInstructor(file);
                break;
            default:
                throw new IllegalArgumentException("Invalid upload type selected");
        }
    }

//    // ----------------- Function Upload Project Details ----------------- //
//    private void processProjectDetails(MultipartFile file) throws Exception {
//        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
//            String line;
//            int rowIndex = 0;
//
//            int currentYear = LocalDate.now().getYear();
//
//            while ((line = br.readLine()) != null) {
//                rowIndex++;
//                if (rowIndex < 9) continue;
//
//                String[] values = line.split(",");
//                if (values.length < 5) continue;
//
//                String projectId = values[0].trim();
//                String projectTitle = values[1].trim();
//                String projectDescription = values[2].trim();
//                String projectCategory = values[3].trim();
//
//                // ตรวจสอบว่า projectId มีค่า ถ้าไม่มีจะข้ามแถวนี้ไป
//                if (projectId.isEmpty()) {
//                    continue;  // ถ้าไม่มี projectId ข้ามแถวนี้ไป
//                }
//
//                // ใช้ค่าจาก projectId เพื่อดึง "program" ซึ่งคือส่วนแรกของ projectId ก่อน SP
//                String program = projectId.split(" ")[0]; // แยกค่าโดยช่องว่างและใช้ส่วนแรก
//
//                Project project = new Project(projectId, program, String.valueOf(currentYear), projectTitle, projectCategory, projectDescription, LocalDateTime.now(), LocalDateTime.now(), null, null);
//                projectRepository.save(project);
//            }
//        } catch (IOException e) {
//            throw new Exception("Error reading CSV file: " + e.getMessage());
//        }
//    }
//
//
//    // ----------------- Function Upload Project Student ----------------- //
//    private void processProjectStudent(MultipartFile file) throws Exception {
//        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
//            String line;
//            int rowIndex = 0;
//
//            // สร้าง Map เพื่อเก็บข้อมูล projectId และลิสต์ของนักศึกษาที่มี projectId เดียวกัน
//            Map<String, List<StudentProject>> projectStudentMap = new HashMap<>();
//
//            // ตัวแปรเพื่อเก็บข้อมูลของนักศึกษาคนแรกในกลุ่ม
//            String currentProjectId = null;
//            String currentProjectTitle = null;
//            String currentProjectDescription = null;
//
//            // ใช้ฟังก์ชันในการดึงค่า student_pj_id ล่าสุดจากฐานข้อมูล
//            int studentIdCounter = generateNextStudentPjId();  // เรียกฟังก์ชันเพื่อดึงค่ารหัสใหม่
//
//            while ((line = br.readLine()) != null) {
//                rowIndex++;
//
//                // ตรวจสอบว่าแถวมีข้อมูลครบถ้วนหรือไม่
//                if (line.trim().isEmpty()) continue;
//
//                String[] values = line.split(",");
//                if (values.length < 10) continue;  // ข้ามแถวที่มีข้อมูลไม่ครบ
//
//                String studentId = values[4].trim();  // student_id
//                String studentName = values[5].trim();
//                String section = values[6].trim();
//                String track = values[7].trim();
//                String projectId = values[0].trim(); // projectId
//                String projectTitle = values[1].trim(); // projectTitle
//                String projectDescription = values[2].trim(); // projectDescription
//
//                // ข้ามแถวที่ไม่มี studentId หรือ studentName
//                if (studentId.isEmpty() || studentName.isEmpty()) continue;
//
//                Optional<Student> existingStudent = studentRepository.findById(studentId);
//                if (!existingStudent.isPresent()) {
//                    continue;  // ถ้าไม่พบ Student ให้ข้ามแถวนี้ไป
//                }
//
//                // ตรวจสอบว่า projectId, projectTitle, projectDescription เป็นค่าว่าง (สำหรับนักศึกษาคนที่ 2 และ 3), ใช้ค่าจากนักศึกษาคนแรก
//                if (projectId.isEmpty()) {
//                    projectId = currentProjectId; // ใช้ projectId จากคนแรก
//                } else {
//                    currentProjectId = projectId; // เก็บ projectId จากคนแรก
//                }
//
//                if (projectTitle.isEmpty()) {
//                    projectTitle = currentProjectTitle; // ใช้ projectTitle จากคนแรก
//                } else {
//                    currentProjectTitle = projectTitle; // เก็บ projectTitle จากคนแรก
//                }
//
//                if (projectDescription.isEmpty()) {
//                    projectDescription = currentProjectDescription; // ใช้ projectDescription จากคนแรก
//                } else {
//                    currentProjectDescription = projectDescription; // เก็บ projectDescription จากคนแรก
//                }
//
//                // ตรวจสอบว่า projectId มีค่าหรือไม่ก่อนบันทึกลงฐานข้อมูล
//                if (projectId == null || projectId.isEmpty()) {
//                    System.out.println("Skipping row due to missing projectId at row index " + rowIndex);
//                    continue; // ถ้าไม่มี projectId ให้ข้ามแถวนี้ไป
//                }
//
//                // ค้นหา Project ในฐานข้อมูล
//                Optional<Project> existingProject = projectRepository.findById(projectId);
//                if (!existingProject.isPresent()) {
//                    System.out.println("Project with ID " + projectId + " not found in Project entity, skipping row...");
//                    continue;  // ถ้าไม่พบ Project ให้ข้ามแถวนี้ไป
//                }
//
//                // สร้าง StudentProject object
//                StudentProject studentProject = new StudentProject();
//                studentProject.setStudent(existingStudent.get());
//                studentProject.setProject(existingProject.get());
//                studentProject.setStatus("Active");
//
//                // กำหนด student_pj_id ใหม่
//                String newStudentPjId = "SP" + String.format("%03d", studentIdCounter++);  // สร้างรหัสใหม่
//                studentProject.setStudentPjId(newStudentPjId);  // ตั้งค่ารหัสใหม่นี้
//
//                // ตรวจสอบว่า projectId นี้มีนักศึกษากี่คนใน Map
//                if (!projectStudentMap.containsKey(projectId)) {
//                    projectStudentMap.put(projectId, new ArrayList<>());
//                }
//
//                List<StudentProject> studentList = projectStudentMap.get(projectId);
//
//                // ถ้ามี 3 คนแล้ว, ให้ไปเก็บใน projectId ใหม่
//                if (studentList.size() < 3) {
//                    studentList.add(studentProject);
//                } else {
//                    // ถ้ามีแล้ว 3 คน ให้ย้ายไปเก็บใน projectId ใหม่
//                    String nextProjectId = String.valueOf(generateNextStudentPjId());  // ใช้ generateNextStudentPjId สำหรับ projectId ใหม่
//                    projectStudentMap.put(nextProjectId, new ArrayList<>());
//                    projectStudentMap.get(nextProjectId).add(studentProject);
//                }
//            }
//
//            // บันทึกข้อมูลทั้งหมดในฐานข้อมูล
//            if (!projectStudentMap.isEmpty()) {
//                for (Map.Entry<String, List<StudentProject>> entry : projectStudentMap.entrySet()) {
//                    for (StudentProject studentProject : entry.getValue()) {
//                        studentProjectRepository.save(studentProject);
//                    }
//                }
//            }
//
//        } catch (IOException e) {
//            throw new Exception("Error reading CSV file: " + e.getMessage());
//        }
//    }
//
//    // ฟังก์ชันในการดึงรหัส student_pj_id ล่าสุดจากฐานข้อมูล
//    private int generateNextStudentPjId() {
//        String latestId = studentProjectRepository.findLatestStudentPjId();
//
//        // ถ้าไม่พบรหัสล่าสุด (กรณีฐานข้อมูลยังว่างเปล่า)
//        if (latestId == null) {
//            return 61;  // เริ่มต้นที่ SP061
//        }
//
//        // เอารหัสล่าสุดออกมาจาก SPxxxx และเพิ่ม 1
//        String numericPart = latestId.substring(2);  // เอาส่วนเลขออกจาก SPxxxx
//        return Integer.parseInt(numericPart) + 1;  // ส่งกลับค่ารหัสใหม่
//    }

    // ----------------- Function Upload Project Details ----------------- //
    private void processProjectDetails(MultipartFile file) throws Exception {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            int rowIndex = 0;

            int currentYear = LocalDate.now().getYear();
            String semester = (LocalDate.now().getMonthValue() <= 6) ? String.valueOf(currentYear) : String.valueOf(currentYear - 1);

            // สร้าง Map เพื่อเก็บข้อมูล projectId และลิสต์ของนักศึกษาที่มี projectId เดียวกัน
            Map<String, List<StudentProject>> projectStudentMap = new HashMap<>();

            while ((line = br.readLine()) != null) {
                rowIndex++;

                // ตรวจสอบว่าแถวมีข้อมูลครบถ้วนหรือไม่
                if (line.trim().isEmpty()) continue;

                String[] values = line.split(",");
                if (values.length < 10) continue;  // ข้ามแถวที่มีข้อมูลไม่ครบ

                String program = values[7].trim(); // program (คอลัมน์ 7)
                String projectTitle = values[2].trim(); // projectTitle (คอลัมน์ 2)
                String projectDescription = values[3].trim(); // projectDescription (คอลัมน์ 3)
                String projectCategory = values[4].trim(); // projectCategory (คอลัมน์ 4)
                String advisor = values[8].trim(); // Advisor (คอลัมน์ 8)
                String studentId = values[5].trim(); // studentId (คอลัมน์ 5)
                String studentName = values[6].trim(); // studentName (คอลัมน์ 6)

                // Generate Project ID โดยใช้ฟังก์ชันใหม่
                String projectId = generateProjectId(program, advisor);

                // ตรวจสอบว่า projectId มีค่าหรือไม่ก่อนบันทึกลงฐานข้อมูล
                if (projectId == null || projectId.isEmpty()) {
                    System.out.println("Skipping row due to missing projectId at row index " + rowIndex);
                    continue; // ถ้าไม่มี projectId ให้ข้ามแถวนี้ไป
                }

                // สร้าง Project object
                Project project = new Project();
                project.setProjectId(projectId);
                project.setProgram(program);
                project.setSemester(semester); // ตั้งค่า semester ตามเงื่อนไข
                project.setProjectTitle(projectTitle);
                project.setProjectCategory(projectCategory);
                project.setProjectDescription(projectDescription);
                project.setRecordedOn(LocalDateTime.now()); // บันทึกเวลาที่ข้อมูลถูกเพิ่ม
                project.setEditedOn(LocalDateTime.now());  // ตั้งค่าเวลาที่แก้ไขล่าสุด


                // บันทึก Project ลงฐานข้อมูล
                projectRepository.save(project);

                // อัปโหลดข้อมูล ProjectInstructorRole
                processProjectInstructorRole(projectId, advisor);  // ส่ง projectId และ advisor เพื่ออัปโหลดข้อมูลไปยัง ProjectInstructorRole

                // อัปโหลดข้อมูล StudentProject
                processStudentProject(projectId, studentId, studentName);  // ส่ง projectId, studentId, studentName เพื่ออัปโหลดข้อมูลลงใน StudentProject
            }

        } catch (IOException e) {
            throw new Exception("Error reading CSV file: " + e.getMessage());
        }
    }

    // ----------------- Function to Generate Instructor ID ----------------- //
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

    // ----------------- Function to Generate Project ID ----------------- //
    private String generateProjectId(String program, String advisor) throws Exception {
        // เช็คว่า Program เป็น ICT หรือ DST
        String prefix = "";
        if ("ICT".equalsIgnoreCase(program)) {
            prefix = "ICT";
        } else if ("DST".equalsIgnoreCase(program)) {
            prefix = "DST";
        } else {
            throw new Exception("Invalid Program: " + program);
        }

        // ดึงรหัสล่าสุดที่มี Program และ Advisor
        String latestProjectId = projectRepository.findLatestProjectIdByProgram(program);

        // ถ้าไม่พบรหัสล่าสุด (กรณีฐานข้อมูลยังว่างเปล่า)
        int nextId = 1;
        if (latestProjectId != null) {
            String numericPart = latestProjectId.substring(prefix.length());  // เอาส่วนเลขออกจาก ICTxxxx หรือ DSTxxxx
            nextId = Integer.parseInt(numericPart) + 1;  // เพิ่มค่าของ ID ขึ้น
        }

        // สร้าง projectId ใหม่
        String newProjectId = prefix + String.format("%04d", nextId);  // สร้าง projectId โดยใช้ prefix ICT หรือ DST และเพิ่มเลข ID

        // ตรวจสอบว่า projectId นี้มีในฐานข้อมูลหรือไม่
        while (projectRepository.existsById(newProjectId)) {
            nextId++;  // ถ้า projectId ซ้ำให้เพิ่มขึ้น
            newProjectId = prefix + String.format("%04d", nextId);  // สร้าง projectId ใหม่
        }

        return newProjectId;  // ส่งกลับ projectId ที่ไม่ซ้ำ
    }

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

    // ----------------- Function Upload Student Project ----------------- //
    private void processStudentProject(String projectId, String studentId, String studentName) throws Exception {
        // ตรวจสอบว่า studentId และ studentName มีในฐานข้อมูล Student หรือไม่
        Optional<Student> existingStudent = studentRepository.findById(studentId);
        if (!existingStudent.isPresent()) {
            // ถ้าไม่พบ StudentId ใน Entity Student
            throw new Exception("Student with ID " + studentId + " not found in Student entity.");
        }

        // ตรวจสอบว่า studentName ตรงกับข้อมูลในฐานข้อมูลหรือไม่
        Student student = existingStudent.get();
        if (!student.getStudentName().equals(studentName)) {
            // ถ้าชื่อไม่ตรงกัน
            throw new Exception("Student name " + studentName + " does not match with the Student ID " + studentId);
        }

        // Generate StudentProjectId ใหม่
        String studentProjectId = "SP" + String.format("%03d", generateNextStudentPjId());

        // สร้าง StudentProject object
        StudentProject studentProject = new StudentProject();
        studentProject.setStudentPjId(studentProjectId);  // กำหนด studentprojectId ที่ได้จากการ Generate
        studentProject.setStudent(existingStudent.get());  // กำหนด studentId ที่ค้นหาในฐานข้อมูล
        studentProject.getProject().setProjectId(projectId);  // กำหนด projectId ที่ได้รับจากการ Generate
        studentProject.setStatus("Active");  // กำหนด status เป็น "Active"

        // บันทึก StudentProject ลงฐานข้อมูล
        studentProjectRepository.save(studentProject);
    }

    // ----------------- Function Upload Project Instructor Role ----------------- //
    private void processProjectInstructorRole(String projectId, String advisor) throws Exception {
        // Generate InstructorId ใหม่
        String instructorId = generateNextInstructorId();

        // ดึงข้อมูล professorId จาก Entity Instructor โดยเทียบค่า Advisor
        Optional<Instructor> instructor = instructorRepository.findByProfessorName(advisor);

        if (!instructor.isPresent()) {
            // ถ้าไม่พบค่า Advisor ใน Entity Instructor ให้แสดงแจ้งเตือน
            throw new Exception("Professor " + advisor + " not found in Instructor entity.");
        }

        // ได้ professorId จาก Entity Instructor
        String professorId = instructor.get().getProfessorId();

        // สร้าง ProjectInstructorRole object
        ProjectInstructorRole projectInstructorRole = new ProjectInstructorRole();
        projectInstructorRole.setInstructorId(instructorId);  // กำหนด InstructorId
        projectInstructorRole.setAssignDate(LocalDateTime.now());  // กำหนด assign_date เป็นเวลาปัจจุบัน
        projectInstructorRole.setRole("Advisor");  // กำหนด role เป็น "Advisor"
        projectInstructorRole.getProjectIdRole().setProjectId(projectId);  // กำหนด projectId ที่ได้รับ
        projectInstructorRole.getInstructor().setProfessorId(professorId);  // กำหนด professorId ที่ได้จากการค้นหาผู้สอน

        // บันทึก ProjectInstructorRole ลงฐานข้อมูล
        projectInstructorRoleRepository.save(projectInstructorRole);
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

//    // ฟังก์ชันในการสร้าง instructor_id ใหม่
//    private String generateNextInstructorId() {
//        // ค้นหาค่ารหัสล่าสุดที่มีอยู่ในฐานข้อมูล
//        String latestId = projectInstructorRoleRepository.findLatestInstructorId();
//
//        // ถ้าไม่พบรหัสล่าสุด (กรณีฐานข้อมูลยังว่างเปล่า)
//        if (latestId == null) {
//            return "INST001";  // เริ่มต้นที่ INST001
//        }
//
//        // เอารหัสล่าสุดออกมาจาก INSTxxxx และเพิ่ม 1
//        String numericPart = latestId.substring(4);  // เอาส่วนเลขออกจาก INSTxxxx
//        int nextId = Integer.parseInt(numericPart) + 1;  // เพิ่ม 1
//        return String.format("INST%03d", nextId);  // ใช้ String.format เพื่อให้รหัสใหม่มีรูปแบบเหมือนเดิม เช่น INST002, INST003, ...
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

}