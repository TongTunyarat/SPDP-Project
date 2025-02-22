package com.example.project.DTO.ShowProject;

public class StudentProjectPropGradeDTO {
    private String studentId;
    private String studentName;
    private String status;
    private boolean isEvaluationComplete;
    private InstructorEvaluationStatusDTO instructorEvaluation;

    public StudentProjectPropGradeDTO(String studentId, String studentName, String status, boolean isEvaluationComplete, InstructorEvaluationStatusDTO instructorEvaluation) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.status = status;
        this.isEvaluationComplete = isEvaluationComplete;
        this.instructorEvaluation = instructorEvaluation;
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

    public InstructorEvaluationStatusDTO getInstructorEvaluation() {
        return instructorEvaluation;
    }

    public void setInstructorEvaluation(InstructorEvaluationStatusDTO instructorEvaluation) {
        this.instructorEvaluation = instructorEvaluation;
    }
}