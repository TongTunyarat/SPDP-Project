package com.example.project.service.ProjectManagement;

import com.example.project.entity.StudentProject;
import com.example.project.repository.StudentProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentProjectService {

    @Autowired
    private StudentProjectRepository studentProjectRepository;

    // บันทึกข้อมูลนักศึกษาในโปรเจกต์
    public void saveStudentProject(StudentProject studentProject) {
        studentProjectRepository.save(studentProject);
    }
}
