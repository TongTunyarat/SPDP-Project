package com.example.project.DTO.Dashboard;

public class EvaluationStatusResponse {
    private int totalProjects;
    private int completedProjects;

    public EvaluationStatusResponse(int totalProjects, int completedProjects) {
        this.totalProjects = totalProjects;
        this.completedProjects = completedProjects;
    }

    public int getTotalProjects() {
        return totalProjects;
    }

    public void setTotalProjects(int totalProjects) {
        this.totalProjects = totalProjects;
    }

    public int getCompletedProjects() {
        return completedProjects;
    }

    public void setCompletedProjects(int completedProjects) {
        this.completedProjects = completedProjects;
    }

}
