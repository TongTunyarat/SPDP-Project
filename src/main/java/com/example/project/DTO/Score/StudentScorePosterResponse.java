package com.example.project.DTO.Score;

import java.util.List;

public class StudentScorePosterResponse {
    private List<ScoreDetail> scores;
    private double rawTotalScore;  // คะแนนเต็มก่อนคิด %
    private double totalScore;

    public StudentScorePosterResponse() {
    }

    public StudentScorePosterResponse(List<ScoreDetail> scores, double rawTotalScore, double totalScore) {
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

    public double getRawTotalScore() {
        return rawTotalScore;
    }

    public void setRawTotalScore(double rawTotalScore) {
        this.rawTotalScore = rawTotalScore;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }
}
