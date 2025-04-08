package com.example.project.DTO.projectManagement;

import com.example.project.entity.Account;

import java.time.LocalDateTime;

public class ProjectDTO {
    private String projectId;
    private String program;
    private String semester;
    private String projectTitle;
    private String projectCategory;
    private String projectDescription;
    private String role;
    private String professorName;

    public ProjectDTO(String projectId, String program, String semester, String projectTitle,
                      String projectCategory, String projectDescription, String role, String professorName) {
        this.projectId = projectId;
        this.program = program;
        this.semester = semester;
        this.projectTitle = projectTitle;
        this.projectCategory = projectCategory;
        this.projectDescription = projectDescription;
        this.role = role;
        this.professorName = professorName;
    }

    // Getter และ Setter
    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getProjectCategory() {
        return projectCategory;
    }

    public void setProjectCategory(String projectCategory) {
        this.projectCategory = projectCategory;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }
}
