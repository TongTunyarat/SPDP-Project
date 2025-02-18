package com.example.project.DTO;

import com.example.project.entity.Project;

public class DefenseEvaScoreDTO {

    private String defenseEvaScoreId;
    private String studentId;
    private String studentName;
    private String projectId;
    private String criteriaId;
    private String criteriaName;
    private Double score;

    public DefenseEvaScoreDTO(String defenseEvaScoreId, String studentId, String studentName, String projectId, String criteriaId, String criteriaName, Double score) {
        this.defenseEvaScoreId = defenseEvaScoreId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.projectId = projectId;
        this.criteriaId = criteriaId;
        this.criteriaName = criteriaName;
        this.score = score;
    }

    public String getdefenseEvaScoreId() {
        return defenseEvaScoreId;
    }

    public void setdefenseEvaScoreId(String proposalEvalScoreId) {
        this.defenseEvaScoreId = proposalEvalScoreId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getCriteriaId() {
        return criteriaId;
    }

    public void setCriteriaId(String criteriaId) {
        this.criteriaId = criteriaId;
    }

    public String getCriteriaName() {
        return criteriaName;
    }

    public void setCriteriaName(String criteriaName) {
        this.criteriaName = criteriaName;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
