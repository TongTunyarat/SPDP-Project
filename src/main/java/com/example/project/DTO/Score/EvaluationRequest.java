package com.example.project.DTO.Score;

import java.util.List;


public class EvaluationRequest {

    private List<ScoreDTO> scores;
    private String comment;

    public List<ScoreDTO> getScores() {
        return scores;
    }

    public void setScores(List<ScoreDTO> scores) {
        this.scores = scores;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

