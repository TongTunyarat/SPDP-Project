package com.example.project.DTO;

public class ProposalEvaResquestDTO {
    private String projectId;
    private String instructorName;
    private String role;

    public ProposalEvaResquestDTO(String projectId, String instructorName, String role) {
        this.projectId = projectId;
        this.instructorName = instructorName;
        this.role = role;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
