package com.example.project.DTO;

import com.example.project.entity.Project;

public class PosterEvaScoreDTO {

    private String PosterEvaScoreId;
    private String projectId;
    private String criteriaId;
    private String criteriaName;
    private float score;

    public PosterEvaScoreDTO(String posterEvaScoreId, String projectId, String criteriaId, String criteriaName, float score) {
        PosterEvaScoreId = posterEvaScoreId;
        this.projectId = projectId;
        this.criteriaId = criteriaId;
        this.criteriaName = criteriaName;
        this.score = score;
    }

    public String getPosterEvaScoreId() {
        return PosterEvaScoreId;
    }

    public void setPosterEvaScoreId(String posterEvaScoreId) {
        PosterEvaScoreId = posterEvaScoreId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getCriteriaId() {
        return criteriaId;
    }

    public void setCriteriaId(String criteriaId) {
        this.criteriaId = criteriaId;
    }

    public String getCriteriaName() {
        return criteriaName;
    }

    public void setCriteriaName(String criteriaName) {
        this.criteriaName = criteriaName;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }
}
