package com.example.project.service.ProjectManagement;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.example.project.DTO.projectManagement.ProfessorRoleDTO;
import com.example.project.entity.ProjectInstructorRole;
import com.example.project.entity.StudentProject;
import com.example.project.repository.ProjectInstructorRoleRepository;
import com.example.project.repository.StudentProjectRepository;
import com.opencsv.CSVReader;
import com.example.project.entity.Project;
import com.example.project.entity.Student;
import com.example.project.repository.ProjectRepository;
import com.example.project.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.example.project.DTO.projectManagement.ExcelDataDTO;

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

    public Map<String, Object> uploadFile(MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        List<String> errorLogs = new ArrayList<>();
        List<Student> students = new ArrayList<>();
        List<Project> projects = new ArrayList<>();
        List<StudentProject> studentProjects = new ArrayList<>();

        try {
            if (file.getOriginalFilename().endsWith(".csv")) {
                processCSV(file, students, projects, studentProjects, errorLogs);
            } else if (file.getOriginalFilename().endsWith(".xlsx")) {
                processExcel(file, students, projects, studentProjects, errorLogs);
            } else {
                throw new IllegalArgumentException("Unsupported file format");
            }

            validateAndSaveData(students, projects, studentProjects, errorLogs); // ✅ ส่ง studentProjects เข้าไป

            response.put("message", "File processed successfully");
            response.put("errors", errorLogs);
        } catch (Exception e) {
            System.out.println("Error processing file: " + e.getMessage());
            e.printStackTrace();
            response.put("message", "File processing failed");
            response.put("errors", List.of(e.getMessage()));
        }

        return response;
    }

    private void processCSV(MultipartFile file, List<Student> students, List<Project> projects, List<StudentProject> studentProjects, List<String> errorLogs) {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> lines = reader.readAll();
            boolean isFirstLine = true;

            for (String[] data : lines) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                if (data.length < 10) {
                    errorLogs.add("Invalid CSV format: " + Arrays.toString(data));
                    continue;
                }
                mapToEntities(data, students, projects, studentProjects, errorLogs); // ✅ ส่ง studentProjects เข้าไป
            }
        } catch (Exception e) {
            System.out.println("Error processing CSV: " + e.getMessage());
            errorLogs.add("Error processing CSV: " + e.getMessage());
        }
    }

    private void processExcel(MultipartFile file, List<Student> students, List<Project> projects, List<StudentProject> studentProjects, List<String> errorLogs) {
        try {
            EasyExcel.read(file.getInputStream(), ExcelDataDTO.class, new ReadListener<ExcelDataDTO>() {
                @Override
                public void invoke(ExcelDataDTO data, AnalysisContext context) {
                    System.out.println("Excel Data Read: " + data);

                    if (data == null || data.getStudentId() == null || data.getStudentId().isEmpty()) {
                        errorLogs.add("Missing Student ID: " + data);
                        return;
                    }

                    String[] rowData = {
                            data.getProjectId(), data.getProjectTitle(), data.getProjectDescription(),
                            data.getAdvisor(), data.getCommittee(), data.getPosterCommittee(),
                            data.getStudentId(), data.getStudentName(), data.getProgram(),
                            data.getSection(), data.getTrack()
                    };

                    System.out.println("Row Data: " + Arrays.toString(rowData));
                    mapToEntities(rowData, students, projects, studentProjects, errorLogs);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                }
            }).sheet().doRead();
        } catch (Exception e) {
            System.out.println("Error processing Excel: " + e.getMessage());
            errorLogs.add("Error processing Excel: " + e.getMessage());
        }
    }

    private void mapToEntities(String[] data, List<Student> students, List<Project> projects, List<StudentProject> studentProjects, List<String> errorLogs) {
        try {
            System.out.println("📌 Raw data from file: " + Arrays.toString(data));

            if (data.length < 11) {
                errorLogs.add("Invalid data format: " + Arrays.toString(data));
                return;
            }

            if (data[6].isEmpty() || data[7].isEmpty()) {
                errorLogs.add("Missing required Student fields: " + Arrays.toString(data));
                return;
            }

            Project project = new Project();
            project.setProjectId(data[0].trim());
            project.setProjectTitle(data[1].trim());
            project.setProjectDescription(data[2].trim());
            project.setProgram(data[8].trim());
            projects.add(project);

            Student student = new Student();
            student.setStudentId(data[6].trim());
            student.setStudentName(data[7].trim());
            student.setProgram(data[8].trim());
            student.setSection(data[9].isEmpty() ? 0 : Byte.parseByte(data[9].trim()));
            student.setTrack(data[10].trim());
            students.add(student);

            StudentProject studentProject = new StudentProject(student, project);
            studentProject.setStudentPjId(UUID.randomUUID().toString());
            studentProjects.add(studentProject);

            System.out.println("✅ Mapped Student: " + student.getStudentId() + " - " + student.getStudentName());

        } catch (Exception e) {
            errorLogs.add("Data mapping error: " + Arrays.toString(data) + " -> " + e.getMessage());
            System.out.println("Data mapping error: " + Arrays.toString(data) + " -> " + e.getMessage());
        }
    }


    private void validateAndSaveData(List<Student> students, List<Project> projects, List<StudentProject> studentProjects, List<String> errorLogs) {
        List<Student> existingStudents = studentRepository.findAll();
        Set<String> existingStudentIds = new HashSet<>();
        Set<String> existingStudentNames = new HashSet<>();

        for (Student student : existingStudents) {
            existingStudentIds.add(student.getStudentId());
            existingStudentNames.add(student.getStudentName());
        }

        List<Student> validStudents = new ArrayList<>();
        for (Student student : students) {
            if (existingStudentIds.contains(student.getStudentId()) || existingStudentNames.contains(student.getStudentName())) {
                errorLogs.add("Duplicate Student ID or Name: " + student.getStudentId() + " - " + student.getStudentName());
            } else {
                validStudents.add(student);
            }
        }

        studentRepository.saveAll(validStudents);
        projectRepository.saveAll(projects);
        studentProjectRepository.saveAll(studentProjects); // ✅ บันทึกข้อมูล StudentProject

    }

    @Transactional
    public void deleteProjectDetails(String projectId) {
        // ตรวจสอบว่าโปรเจกต์มีอยู่หรือไม่
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found for ID: " + projectId));

        // ลบข้อมูลอาจารย์ที่ปรึกษาที่เกี่ยวข้องกับโปรเจกต์นี้
        List<ProjectInstructorRole> existingRoles = projectInstructorRoleRepository.findByProjectIdRole_ProjectId(projectId);
        if (existingRoles != null && !existingRoles.isEmpty()) {
            projectInstructorRoleRepository.deleteAll(existingRoles);  // ลบอาจารย์ที่ปรึกษาทั้งหมดที่เกี่ยวข้องกับโปรเจกต์นี้
        }

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

}
