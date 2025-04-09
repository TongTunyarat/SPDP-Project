package com.example.project.DTO;

public class DefenseGradeEvaResponseDTO {
    private String evaId;
    private String studentId;
    private String studentName;
    private String criteriaId;
    private String criteriaName;
    private String type;
    private Double score;
    private String comment;

    public DefenseGradeEvaResponseDTO(String evaId, String studentId, String studentName, String criteriaId, String criteriaName, String type, Double score, String comment) {
        this.evaId = evaId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.criteriaId = criteriaId;
        this.criteriaName = criteriaName;
        this.type = type;
        this.score = score;
        this.comment = comment;
    }

    public String getEvaId() {
        return evaId;
    }

    public void setEvaId(String evaId) {
        this.evaId = evaId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

