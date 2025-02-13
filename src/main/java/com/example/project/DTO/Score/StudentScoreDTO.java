package com.example.project.DTO.Score;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class StudentScoreDTO {
    private List<ScoreDetail> scores;
    private double rawTotalScore;  // คะแนนเต็มก่อนคิด %
    private double totalScore;     // คะแนนหลังคิด %

    public StudentScoreDTO() {
    }

    public StudentScoreDTO(List<ScoreDetail> scores, double rawTotalScore, double totalScore) {
        this.scores = scores;
        this.rawTotalScore = rawTotalScore;
        this.totalScore = totalScore;
    }

    public List<ScoreDetail> getScores() {
        return scores;
    }

    public void setScores(List<ScoreDetail> scores) {
        this.scores = scores;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public double getRawTotalScore() {
        return rawTotalScore;
    }

    public void setRawTotalScore(double rawTotalScore) {
        this.rawTotalScore = rawTotalScore;
    }
}
