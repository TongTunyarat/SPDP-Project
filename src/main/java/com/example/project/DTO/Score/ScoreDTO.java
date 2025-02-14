package com.example.project.DTO.Score;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class ScoreDTO {

    private String scoreCriteriaId;
    private BigDecimal score;

    @Override
    public String toString() {
        return "ScoreDTO{" +
                "scoreCriteriaId='" + scoreCriteriaId + '\'' +
                ", score=" + score +
                '}';
    }

    public ScoreDTO(String scoreCriteriaId, BigDecimal score) {
        this.scoreCriteriaId = scoreCriteriaId;
        this.score = score;
    }

    public void setScoreCriteriaId(String scoreCriteriaId) {
        this.scoreCriteriaId = scoreCriteriaId;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public String getScoreCriteriaId() {
        return scoreCriteriaId;
    }

    public BigDecimal getScore() {
        return score;
    }
}
