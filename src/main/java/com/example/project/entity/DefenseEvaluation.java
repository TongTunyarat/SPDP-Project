//package com.example.project.entity;
//
//import jakarta.persistence.*;
//import lombok.Data;
//
//import java.time.LocalDateTime;
//
//@Data
//@Entity
//@Table(name = "DefenseEvaluation")
//public class DefenseEvaluation {
//
//    @Id
//    @Column(name = "defense_eva_id")
//    private String defenseEvaId;
//
//    @Column(name = "recorded_on")
//    private LocalDateTime recordedOn;
//
//    @Column(name = "comment")
//    private String comment;
//
//    @Column(name = "edited_on")
//    private LocalDateTime editedOn;
//
//    @ManyToOne
//    @JoinColumn(name = "recorded_by", referencedColumnName = "user_username")
//    private Account recordedBy;
//
//    @ManyToOne
//    @JoinColumn(name = "edited_by", referencedColumnName = "user_username")
//    private Account editedBy;
//
//    @ManyToOne
//    @JoinColumn(name = "project_id", referencedColumnName = "project_id")
//    private Project projectId;
//
////    @ManyToOne
////    @JoinColumn(name = "instructor_id", referencedColumnName = "instructor_id")
////    private Instructor instructorId;
////
////    @ManyToOne
////    @JoinColumn(name = "student_id", referencedColumnName = "student_id")
////    private Student studentId;
//
//    public String getDefenseEvaId() {
//        return defenseEvaId;
//    }
//
//    public LocalDateTime getRecordedOn() {
//        return recordedOn;
//    }
//
//    public String getComment() {
//        return comment;
//    }
//
//    public LocalDateTime getEditedOn() {
//        return editedOn;
//    }
//
//    public Account getRecordedBy() {
//        return recordedBy;
//    }
//
//    public Account getEditedBy() {
//        return editedBy;
//    }
//
//    public Project getProjectId() {
//        return projectId;
//    }
//
////    public Instructor getInstructorId() {
////        return instructorId;
////    }
////
////    public Student getStudentId() {
////        return studentId;
////    }
//}
