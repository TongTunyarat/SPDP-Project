package com.example.project.DTO;

import com.example.project.entity.StudentProject;

import java.util.List;

public class PosterEvaScoreDTO {

    private String PosterEvaScoreId;
    private String projectId;
    private String criteriaId;
    private String criteriaName;
    private Double score;
    private Float criteriaWeight;
    private String maxScore;


    public PosterEvaScoreDTO(String posterEvaScoreId, String projectId, String criteriaId, String criteriaName, Double score, Float criteriaWeight, String maxScore) {
        PosterEvaScoreId = posterEvaScoreId;
        this.projectId = projectId;
        this.criteriaId = criteriaId;
        this.criteriaName = criteriaName;
        this.score = score;
        this.criteriaWeight = criteriaWeight;
        this.maxScore = maxScore;
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

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Float getCriteriaWeight() {
        return criteriaWeight;
    }

    public void setCriteriaWeight(Float criteriaWeight) {
        this.criteriaWeight = criteriaWeight;
    }

    public String getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(String maxScore) {
        this.maxScore = maxScore;
    }
}
