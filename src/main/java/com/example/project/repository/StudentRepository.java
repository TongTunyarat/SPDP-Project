package com.example.project.repository;

import com.example.project.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, String> {
//    List<Student> findByStudentId(ArrayList<Student> strings);
    // Modify the method to use 'In' for studentId
    List<Student> findByStudentIdIn(List<String> studentIds);

}
