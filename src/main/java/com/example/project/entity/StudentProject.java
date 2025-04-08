package com.example.project.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

@Entity
@Table(name="studentproject")
public class StudentProject {

    @Id
    @Column(name = "student_pj_id")
    private String studentPjId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @JsonManagedReference
    private Student student;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonManagedReference
    private Project project;

//    @Column(name = "project_id")
//    private String projectId;

    @Column(name = "status")
    private String status;

    public StudentProject() {

    }

    public StudentProject(Student student, Project project) {
        this.student = student;
        this.project = project;
    }

    public StudentProject(String studentId, Project project) {
        this.student = new Student(studentId, "", "", "", ""); // สร้าง Student Object ด้วย studentId และค่าที่เหลือสามารถกำหนดภายหลัง
        this.project = project;
    }

    public StudentProject(Project project, String studentId) {
    }

    public String getStudentPjId() {
        return studentPjId;
    }

    public void setStudentPjId(String studentPjId) {
        this.studentPjId = studentPjId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "StudentProject{" +
                "studentPjId='" + studentPjId + '\'' +
                ", student=" + student +
                ", project=" + project +
                ", status='" + status + '\'' +
                '}';
    }
}