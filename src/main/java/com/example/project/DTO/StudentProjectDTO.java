package com.example.project.DTO;

import org.springframework.beans.factory.annotation.Autowired;

public class StudentProjectDTO {
    private String studentId;
    private String studentName;

    public StudentProjectDTO(String studentId, String studentName) {
        this.studentId = studentId;
        this.studentName = studentName;
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
}