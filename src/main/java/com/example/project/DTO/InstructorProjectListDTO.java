package com.example.project.DTO;

public class InstructorProjectListDTO {

    private String instructorId;
    private String role;
    private String projectId;
    private String professorId;
    private String professorName;

    public InstructorProjectListDTO(String instructorId, String role, String projectId, String professorId, String professorName) {
        this.instructorId = instructorId;
        this.role = role;
        this.projectId = projectId;
        this.professorId = professorId;
        this.professorName = professorName;
    }

    // Getters and Setters
    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProfessorId() {
        return professorId;
    }

    public void setProfessorId(String professorId) {
        this.professorId = professorId;
    }

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }
}