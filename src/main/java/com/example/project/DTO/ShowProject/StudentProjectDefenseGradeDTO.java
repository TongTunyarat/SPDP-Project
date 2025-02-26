package com.example.project.DTO.ShowProject;

public class StudentProjectDefenseGradeDTO {
    private String studentId;
    private String studentName;
    private String status;
    private String gradeResult;
    private boolean isEvaluationComplete;
    private InstructorEvaluationDefenseStatusDTO instructorEvaluation;

    public StudentProjectDefenseGradeDTO(String studentId, String studentName, String status, boolean isEvaluationComplete, InstructorEvaluationDefenseStatusDTO instructorEvaluation, String gradeResult) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.status = status;
        this.gradeResult = gradeResult;
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

    public InstructorEvaluationDefenseStatusDTO getInstructorEvaluation() {
        return instructorEvaluation;
    }

    public void setInstructorEvaluation(InstructorEvaluationDefenseStatusDTO instructorEvaluation) {
        this.instructorEvaluation = instructorEvaluation;
    }

    public String getGradeResult() {
        return gradeResult;
    }

    public void setGradeResult(String gradeResult) {
        this.gradeResult = gradeResult;
    }
}