package com.example.project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

@Data
@Entity
@Table(name = "DefenseEvalScore")
public class DefenseEvalScore {

    @Id
    @Column(name = "eval_id")
    private String evalId;

    @Column(name = "score")
    private int score;

//    @ManyToOne
//    @JoinColumn(name = "criteria_id", referencedColumnName = "criteria_id")
//    private Criteria criteriaId;

    @ManyToOne
    @JoinColumn(name = "defense_eva_id", referencedColumnName = "defense_eva_id")
    private DefenseEvaluation defenseEvaId;

    public String getEvalId() {
        return evalId;
    }

    public int getScore() {
        return score;
    }

    public DefenseEvaluation getDefenseEvaId() {
        return defenseEvaId;
    }

//    public Criteria getCriteriaId() {
//        return criteriaId;
//    }
}
