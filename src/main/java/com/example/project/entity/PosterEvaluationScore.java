package com.example.project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "PosterEvalScore")
public class PosterEvaluationScore {

    @Id
    @Column(name = "poster_eva_id")
    private String posterEvaId;

    @Column(name = "score")
    private float score;

    @ManyToOne
    @JoinColumn(name = "criteria_id", referencedColumnName = "criteria_id")
    private Criteria criteriaId;

    @ManyToOne
    @JoinColumn(name = "poster_id", referencedColumnName = "poster_id")
    private PosterEvaluation posterId;


    public String getPosterEvaId() {
        return posterEvaId;
    }

    public Criteria getCriteriaId() {
        return criteriaId;
    }

    public float getScore() {
        return score;
    }

    public PosterEvaluation getPosterId() {
        return posterId;
    }
}

