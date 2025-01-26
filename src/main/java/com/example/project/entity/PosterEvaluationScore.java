package com.example.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "posterevalscore")
public class PosterEvaluationScore {

    @Id
    @Column(name = "poster_eva_id")
    private String posterEvaId;

    @Column(name = "score")
    private float score;

    // criteria_id
    @ManyToOne
    @JoinColumn(name = "criteria_id")
    @JsonManagedReference
    private Criteria criteriaPoster;

    // poster_id
    @ManyToOne
    @JoinColumn(name = "poster_id")
    @JsonBackReference
    private PosterEvaluation posterEvaluation;

    public String getPosterEvaId() {
        return posterEvaId;
    }

    public void setPosterEvaId(String posterEvaId) {
        this.posterEvaId = posterEvaId;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public Criteria getCriteriaPoster() {
        return criteriaPoster;
    }

    public void setCriteriaPoster(Criteria criteriaPoster) {
        this.criteriaPoster = criteriaPoster;
    }

    public PosterEvaluation getPosterEvaluation() {
        return posterEvaluation;
    }

    public void setPosterEvaluation(PosterEvaluation posterEvaluation) {
        this.posterEvaluation = posterEvaluation;
    }
}

