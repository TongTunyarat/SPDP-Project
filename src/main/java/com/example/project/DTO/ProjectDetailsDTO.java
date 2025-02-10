package com.example.project.DTO;

import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

public class ProjectDetailsDTO {
    private String projectId;
    private String projectName;
    private String projectTitle;
    private String professorName;
    private LocalDateTime dateTime;

   public ProjectDetailsDTO(String projectId, String projectName, String projectTitle, String professorName, LocalDateTime dateTime) {
       this.projectId = projectId;
       this.projectName = projectName;
       this.projectTitle = projectTitle;
       this.professorName = professorName;
       this.dateTime = dateTime;
   }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
