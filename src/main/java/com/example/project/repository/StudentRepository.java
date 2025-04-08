package com.example.project.repository;

import com.example.project.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import java.util.ArrayList;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, String> {

    List<Student> findAllByStudentId(String studentId); // เปลี่ยนจากการคืนค่าค่าเดียวเป็น List
    List<Student> findAllByStudentName(String studentName); // เปลี่ยนจากการคืนค่าค่าเดียวเป็น List

    Student findByStudentId(String studentId);

    @Query("SELECT s FROM Student s WHERE NOT EXISTS (SELECT 1 FROM StudentProject sp WHERE sp.student.studentId = s.studentId)")
    List<Student> findStudentsWithoutProject();

    List<Student> findAll();

//    List<Student> findByStudentId(ArrayList<Student> strings);
    // Modify the method to use 'In' for studentId
    List<Student> findByStudentIdIn(List<String> studentIds);

}
