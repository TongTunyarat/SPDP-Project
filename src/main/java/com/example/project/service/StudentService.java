//package com.example.project.service;
//
//import com.example.project.entity.Student;
//import com.example.project.entity.StudentProject;
//import com.example.project.repository.StudentRepository;
//import com.example.project.repository.StudentProjectRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class StudentService {
//
//    private final StudentProjectRepository studentProjectRepository;
//    private final StudentRepository studentRepository;
//
//    @Autowired
//
//    public StudentService(StudentProjectRepository studentProjectRepository, StudentRepository studentRepository) {
//        this.studentProjectRepository = studentProjectRepository;
//        this.studentRepository = studentRepository;
//    }
//
//    // student
//    public List<Student> getAllStudent() {
//        return studentRepository.findAll();
//    }
//
//    // StudentProject
//    public List<StudentProject> getAllStudentProject() {
//        return studentProjectRepository.findAll();
//    }
//}
