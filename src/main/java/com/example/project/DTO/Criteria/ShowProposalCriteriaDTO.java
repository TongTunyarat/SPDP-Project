package com.example.project.DTO.Criteria;

public class ShowProposalCriteriaDTO {
    private String criteriaId;
    private String criteriaName;
    private String criteriaNameTH;
    private String maxScore;
    private String type;

    public ShowProposalCriteriaDTO(String criteriaId, String criteriaName, String criteriaNameTH, String maxScore, String type) {
        this.criteriaId = criteriaId;
        this.criteriaName = criteriaName;
        this.criteriaNameTH = criteriaNameTH;
        this.maxScore = maxScore;
        this.type = type;
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

    public String getCriteriaNameTH() {
        return criteriaNameTH;
    }

    public void setCriteriaNameTH(String criteriaNameTH) {
        this.criteriaNameTH = criteriaNameTH;
    }

    public String getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(String maxScore) {
        this.maxScore = maxScore;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}