package com.example.project.DTO.Score;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class StudentScoreResponse {
    private String studentId;
    private String studentName;
    private List<ScoreDetail> scores;
    private double rawTotalScore;  // คะแนนเต็มก่อนคิด %
    private double totalScore;     // คะแนนหลังคิด %

    public StudentScoreResponse() {
    }

    public StudentScoreResponse(String studentId, String studentName, List<ScoreDetail> scores, double rawTotalScore, double totalScore) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.scores = scores;
        this.rawTotalScore = rawTotalScore;
        this.totalScore = totalScore;
    }

    public double getRawTotalScore() {
        return rawTotalScore;
    }

    public void setRawTotalScore(double rawTotalScore) {
        this.rawTotalScore = rawTotalScore;
    }

    public String getStudentId() {
        return studentId;
    }

    public StudentScoreResponse setStudentId(String studentId) {
        this.studentId = studentId;
        return this;
    }

    public String getStudentName() {
        return studentName;
    }

    public StudentScoreResponse setStudentName(String studentName) {
        this.studentName = studentName;
        return this;
    }

    public List<ScoreDetail> getScores() {
        return scores;
    }

    public StudentScoreResponse setScores(List<ScoreDetail> scores) {
        this.scores = scores;
        return this;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public StudentScoreResponse setTotalScore(double totalScore) {
        this.totalScore = totalScore;
        return this;
    }
}

