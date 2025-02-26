package com.example.project.DTO.Score;

import java.util.List;

public class DefenseScoreRequestDTO {

    private int defenseScore;
    private int posterScore;
    private String grade;
    private int totalScore;
    private List<ScoreDTO> scores;

    @Override
    public String toString() {
        return "DefenseScoreRequestDTO{" +
                "defenseScore=" + defenseScore +
                ", posterScore=" + posterScore +
                ", grade='" + grade + '\'' +
                ", totalScore=" + totalScore +
                ", scores=" + scores +
                '}';
    }

    public int getDefenseScore() {
        return defenseScore;
    }

    public void setDefenseScore(int defenseScore) {
        this.defenseScore = defenseScore;
    }

    public int getPosterScore() {
        return posterScore;
    }

    public void setPosterScore(int posterScore) {
        this.posterScore = posterScore;
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
