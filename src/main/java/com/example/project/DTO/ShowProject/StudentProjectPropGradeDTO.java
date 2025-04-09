package com.example.project.DTO.ShowProject;

public class StudentProjectPropGradeDTO {
    private String studentId;
    private String studentName;
    private String status;
    private String gradeResult;
    private boolean isEvaluationComplete;
    private String semester;
    private InstructorEvaluationStatusDTO instructorEvaluation;

    public StudentProjectPropGradeDTO(String studentId, String studentName, String status, boolean isEvaluationComplete, InstructorEvaluationStatusDTO instructorEvaluation, String gradeResult) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.status = status;
        this.gradeResult = gradeResult;
        this.semester = semester;
        this.isEvaluationComplete = isEvaluationComplete;
        this.instructorEvaluation = instructorEvaluation;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
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

    public String getGradeResult() {
        return gradeResult;
    }

    public void setGradeResult(String gradeResult) {
        this.gradeResult = gradeResult;
    }
}