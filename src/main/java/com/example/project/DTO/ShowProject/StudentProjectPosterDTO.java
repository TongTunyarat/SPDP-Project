package com.example.project.DTO.ShowProject;

public class StudentProjectPosterDTO {
    private String studentId;
    private String studentName;
    private String status;
    private boolean isEvaluationComplete;

    public StudentProjectPosterDTO(String studentId, String studentName, String status, boolean isEvaluationComplete) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.status = status;
        this.isEvaluationComplete = isEvaluationComplete;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isEvaluationComplete() {
        return isEvaluationComplete;
    }

    public void setEvaluationComplete(boolean evaluationComplete) {
        isEvaluationComplete = evaluationComplete;
    }
}