package com.example.project.DTO;

public class ProjectDetailsDTO {
    private String projectId;
    private String projectTitle;
    private String professorName;

    public ProjectDetailsDTO(String projectId, String projectTitle, String professorName) {
        this.projectId = projectId;
        this.projectTitle = projectTitle;
        this.professorName = professorName;
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

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }
}
