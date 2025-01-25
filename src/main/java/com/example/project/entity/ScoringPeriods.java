//package com.example.project.entity;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.*;
//import lombok.Data;
//
//import java.time.LocalDate;
//
//@Data
//@Entity
//@Table(name = "ScoringPeriods")
//public class ScoringPeriods {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;
//
//    @Column(name = "evaluation_type")
//    private String evaluationType;
//
//    @Column(name = "start_date")
//    private LocalDate startDate;
//
//    @Column(name = "end_date")
//    private LocalDate endDate;
//
//    @ManyToOne
//    @JoinColumn(name = "recorded_by", referencedColumnName = "user_username")
//    private Account recordedBy;
//
//    public Integer getId() {
//        return id;
//    }
//
//    public String getEvaluationType() {
//        return evaluationType;
//    }
//
//    public LocalDate getStartDate() {
//        return startDate;
//    }
//
//    public LocalDate getEndDate() {
//        return endDate;
//    }
//
//    public Account getRecordedBy() {
//        return recordedBy;
//    }
//}
