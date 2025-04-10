package com.example.project.DTO;


import java.math.BigDecimal;

public class ProposalEvalScoreDTO {

    private String proposalEvalScoreId;
    private String studentId;
    private String studentName;
    private String projectId;
    private String criteriaId;
    private String criteriaName;
    private Float criteriaWeight;
    private Double score;
    private String maxScore;
    private String professorId;

    public ProposalEvalScoreDTO(String proposalEvalScoreId, String studentId, String studentName, String projectId, String criteriaId, String criteriaName, Float criteriaWeight, Double score, String maxScore, String professorId) {
        this.proposalEvalScoreId = proposalEvalScoreId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.projectId = projectId;
        this.criteriaId = criteriaId;
        this.criteriaName = criteriaName;
        this.criteriaWeight = criteriaWeight;
        this.score = score;
        this.maxScore = maxScore;
        this.professorId = professorId;
    }

// Getters and Setters


    public String getProfessorId() {
        return professorId;
    }

    public void setProfessorId(String professorId) {
        this.professorId = professorId;
    }

    public Float getCriteriaWeight() {
        return criteriaWeight;
    }

    public void setCriteriaWeight(Float criteriaWeight) {
        this.criteriaWeight = criteriaWeight;
    }

    public String getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(String maxScore) {
        this.maxScore = maxScore;
    }

    public String getProposalEvalScoreId() {
        return proposalEvalScoreId;
    }

    public void setProposalEvalScoreId(String proposalEvalScoreId) {
        this.proposalEvalScoreId = proposalEvalScoreId;
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

