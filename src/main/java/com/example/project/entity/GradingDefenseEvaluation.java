//package com.example.project.entity;
//
//import jakarta.persistence.*;
//import lombok.Data;
//
//@Data
//@Entity
//@Table(name = "GradingDefenseEvaluation")
//public class GradingDefenseEvaluation {
//
//    @Id
//    @Column(name = "defense_grade_eva_id")
//    private String defenseGradeEvalId;
//
//    @Column(name = "datetime")
//    private String datetime;
//
//    @Column(name = "avg_poster_score")
//    private double avgPosterScore;
//
//    @Column(name = "avg_score_defense")
//    private double avgScoreDefense;
//
//    @Column(name = "evaluate_score")
//    private double evaluateScore;
//
//    @Column(name = "extra_score")
//    private double extraScore;
//
//    @Column(name = "total_score")
//    private double totalScore;
//
//    @Column(name = "grade_result")
//    private String gradeResult;
//
//    @ManyToOne
//    @JoinColumn(name = "project_id", referencedColumnName = "project_id")
//    private Project projectId;
//
////    @ManyToOne
////    @JoinColumn(name = "student_id", referencedColumnName = "student_id")
////    private Student studentId;
//
//    public String getDefenseGradeEvalId() {
//        return defenseGradeEvalId;
//    }
//
//    public String getDatetime() {
//        return datetime;
//    }
//
//    public double getAvgPosterScore() {
//        return avgPosterScore;
//    }
//
//    public double getAvgScoreDefense() {
//        return avgScoreDefense;
//    }
//
//    public double getEvaluateScore() {
//        return evaluateScore;
//    }
//
//    public double getExtraScore() {
//        return extraScore;
//    }
//
//    public double getTotalScore() {
//        return totalScore;
//    }
//
//    public String getGradeResult() {
//        return gradeResult;
//    }
//
//    public Project getProjectId() {
//        return projectId;
//    }
//
////    public Student getStudentId() {
////        return studentId;
////    }
//}
