package com.example.project.DTO.ShowProject;

import java.util.List;

public class InstructorProjectPropGradeDTO {

    private String projectProgram;
    private String projectId;
    private String projectTitle;
    private String role;
    private List<StudentProjectPropGradeDTO> students;
    private String semester;

    private boolean isAllEvaluationsComplete;

    public InstructorProjectPropGradeDTO(String projectProgram, String projectId, String projectTitle, String role, List<StudentProjectPropGradeDTO> students, boolean isAllEvaluationsComplete, String semester) {
        this.projectProgram = projectProgram;
        this.projectId = projectId;
        this.projectTitle = projectTitle;
        this.role = role;
        this.students = students;
        this.isAllEvaluationsComplete = isAllEvaluationsComplete;
        this.semester = semester;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getProjectProgram() {
        return projectProgram;
    }

    public void setProjectProgram(String projectProgram) {
        this.projectProgram = projectProgram;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<StudentProjectPropGradeDTO> getStudents() {
        return students;
    }

    public void setStudents(List<StudentProjectPropGradeDTO> students) {
        this.students = students;
    }

    public boolean isAllEvaluationsComplete() {
        return isAllEvaluationsComplete;
    }

    public void setAllEvaluationsComplete(boolean allEvaluationsComplete) {
        isAllEvaluationsComplete = allEvaluationsComplete;
    }
}