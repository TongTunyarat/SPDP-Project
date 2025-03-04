package com.example.project.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.opencsv.CSVReader;
import com.example.project.entity.Project;
import com.example.project.entity.Student;
import com.example.project.repository.ProjectRepository;
import com.example.project.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.project.DTO.projectManagement.ExcelDataDTO;

import java.io.InputStreamReader;
import java.util.*;

@Service
@Slf4j
public class UploadFilesService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private StudentRepository studentRepository;

    public Map<String, Object> uploadFile(MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        List<String> errorLogs = new ArrayList<>();
        List<Student> students = new ArrayList<>();
        List<Project> projects = new ArrayList<>();

        try {
            if (file.getOriginalFilename().endsWith(".csv")) {
                processCSV(file, students, projects, errorLogs);
            } else if (file.getOriginalFilename().endsWith(".xlsx")) {
                processExcel(file, students, projects, errorLogs);
            } else {
                throw new IllegalArgumentException("Unsupported file format");
            }

            validateAndSaveData(students, projects, errorLogs);

            response.put("message", "File processed successfully");
            response.put("errors", errorLogs);
        } catch (Exception e) {
            // ใช้ System.out.println() แสดงผลแทนการใช้ log
            System.out.println("Error processing file: " + e.getMessage());
            response.put("message", "File processing failed");
            response.put("errors", List.of(e.getMessage()));
        }

        return response;
    }

    private void processCSV(MultipartFile file, List<Student> students, List<Project> projects, List<String> errorLogs) {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> lines = reader.readAll();
            boolean isFirstLine = true;

            for (String[] data : lines) {
                if (isFirstLine) { // ข้าม Header
                    isFirstLine = false;
                    continue;
                }
                if (data.length < 10) {
                    errorLogs.add("Invalid CSV format: " + Arrays.toString(data));
                    continue;
                }
                mapToEntities(data, students, projects, errorLogs);
            }
        } catch (Exception e) {
            // ใช้ System.out.println() แสดงผลแทนการใช้ log
            System.out.println("Error processing CSV: " + e.getMessage());
            errorLogs.add("Error processing CSV: " + e.getMessage());
        }
    }

    private void processExcel(MultipartFile file, List<Student> students, List<Project> projects, List<String> errorLogs) {
        try {
            EasyExcel.read(file.getInputStream(), ExcelDataDTO.class, new ReadListener<ExcelDataDTO>() {
                @Override
                public void invoke(ExcelDataDTO data, AnalysisContext context) {
                    String[] rowData = {
                            data.getProjectID(), data.getProjectTitle(), data.getProjectDescription(),
                            data.getAdvisor(), data.getCommittee(), data.getPosterCommittee(),
                            data.getStudentID(), data.getStudentName(), data.getProgram(),
                            data.getSection(), data.getTrack()
                    };
                    mapToEntities(rowData, students, projects, errorLogs);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {}
            }).sheet().doRead();
        } catch (Exception e) {
            // ใช้ System.out.println() แสดงผลแทนการใช้ log
            System.out.println("Error processing Excel: " + e.getMessage());
            errorLogs.add("Error processing Excel: " + e.getMessage());
        }
    }

    private void mapToEntities(String[] data, List<Student> students, List<Project> projects, List<String> errorLogs) {
        try {
            Project project = new Project();
            project.setProjectId(data[0]);
            project.setProjectTitle(data[1]);
            project.setProjectDescription(data[2]);
            project.setProgram(data[8]);
            projects.add(project);

            Student student = new Student();
            student.setStudentId(data[6]);
            student.setStudentName(data[7]);
            student.setProgram(data[8]);
            student.setSection(Byte.parseByte(data[9]));
            student.setTrack(data[10]);

            students.add(student);
        } catch (Exception e) {
            errorLogs.add("Data mapping error: " + Arrays.toString(data));
            // ใช้ System.out.println() แสดงผลแทนการใช้ log
            System.out.println("Data mapping error: " + Arrays.toString(data));
        }
    }

    private void validateAndSaveData(List<Student> students, List<Project> projects, List<String> errorLogs) {
        // เปลี่ยนจากการใช้ findAllByStudentId เป็นการดึงข้อมูลทั้งหมด
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

        // บันทึกข้อมูลที่ไม่ซ้ำ
        studentRepository.saveAll(validStudents);
        projectRepository.saveAll(projects);
    }
}
