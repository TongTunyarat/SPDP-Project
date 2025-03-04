package com.example.project.repository;

import com.example.project.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, String> {

    List<Student> findAllByStudentId(String studentId); // เปลี่ยนจากการคืนค่าค่าเดียวเป็น List
    List<Student> findAllByStudentName(String studentName); // เปลี่ยนจากการคืนค่าค่าเดียวเป็น List

}
