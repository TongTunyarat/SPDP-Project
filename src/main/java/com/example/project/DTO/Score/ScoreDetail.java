package com.example.project.DTO.Score;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ScoreDetail {
    private String criteriaId;
    private double score;

    public ScoreDetail() {
    }

    public ScoreDetail(String criteriaId, double score) {
        this.criteriaId = criteriaId;
        this.score = score;
    }

    public String getCriteriaId() {
        return criteriaId;
    }

    public ScoreDetail setCriteriaId(String criteriaId) {
        this.criteriaId = criteriaId;
        return this;
    }

    public double getScore() {
        return score;
    }

    public ScoreDetail setScore(double score) {
        this.score = score;
        return this;
    }
}
