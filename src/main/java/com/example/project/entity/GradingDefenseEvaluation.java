package com.example.project.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "gradingdefenseevaluation")
public class GradingDefenseEvaluation {

    @Id
    @Column(name = "defense_grade_eva_id")
    private String defenseGradeEvalId;

    @Column(name = "datetime")
    private String datetime;

    @Column(name = "avg_poster_score")
    private double avgPosterScore;

    @Column(name = "avg_score_defense")
    private double avgScoreDefense;

    @Column(name = "evaluate_score")
    private double evaluateScore;

    @Column(name = "extra_score")
    private double extraScore;

    @Column(name = "total_score")
    private double totalScore;

    @Column(name = "grade_result")
    private String gradeResult;

    // project_id
    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonManagedReference
    private Project projectId;

/// /    @ManyToOne
/// /    @JoinColumn(name = "student_id", referencedColumnName = "student_id")
/// /    private Student studentId;

    public String getDefenseGradeEvalId() {
        return defenseGradeEvalId;
    }

    public void setDefenseGradeEvalId(String defenseGradeEvalId) {
        this.defenseGradeEvalId = defenseGradeEvalId;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public double getAvgPosterScore() {
        return avgPosterScore;
    }

    public void setAvgPosterScore(double avgPosterScore) {
        this.avgPosterScore = avgPosterScore;
    }

    public double getAvgScoreDefense() {
        return avgScoreDefense;
    }

    public void setAvgScoreDefense(double avgScoreDefense) {
        this.avgScoreDefense = avgScoreDefense;
    }

    public double getEvaluateScore() {
        return evaluateScore;
    }

    public void setEvaluateScore(double evaluateScore) {
        this.evaluateScore = evaluateScore;
    }

    public double getExtraScore() {
        return extraScore;
    }

    public void setExtraScore(double extraScore) {
        this.extraScore = extraScore;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public String getGradeResult() {
        return gradeResult;
    }

    public void setGradeResult(String gradeResult) {
        this.gradeResult = gradeResult;
    }

    public Project getProjectId() {
        return projectId;
    }

    public void setProjectId(Project projectId) {
        this.projectId = projectId;
    }
}
