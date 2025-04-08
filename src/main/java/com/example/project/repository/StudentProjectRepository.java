package com.example.project.repository;

import com.example.project.entity.StudentProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudentProjectRepository extends JpaRepository<StudentProject, String> {

    List<StudentProject> findByProject_ProjectId(String projectId);

    Optional<StudentProject> findByProject_ProjectIdAndStudentStudentId(String projectId, String studentId);

    // ค้นหารหัส student_pj_id ล่าสุด
    @Query("SELECT MAX(s.studentPjId) FROM StudentProject s")
    String findLatestStudentPjId();  // ค้นหาค่ารหัสล่าสุด

    // ฟังก์ชันตรวจสอบว่านักศึกษามีการเชื่อมโยงกับโปรเจกต์หรือไม่
    boolean existsByStudentStudentId(String studentId);

}
