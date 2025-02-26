package com.example.project.DTO.Score;

import java.util.List;

public class ScoreRequestDTO {

    private int scoreResult40;
    private String grade;
    private int totalScore;
    private List<ScoreDTO> scores;

    @Override
    public String toString() {
        return "ScoreRequestDTO{" +
                "scoreProposal=" + scoreResult40 +
                ", grade='" + grade + '\'' +
                ", totalScore=" + totalScore +
                ", scores=" + scores +
                '}';
    }

    public int getScoreProposal() {
        return scoreResult40;
    }

    public void setScoreProposal(int scoreProposal) {
        this.scoreResult40 = scoreProposal;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public List<ScoreDTO> getScores() {
        return scores;
    }

    public void setScores(List<ScoreDTO> scores) {
        this.scores = scores;
    }
}

