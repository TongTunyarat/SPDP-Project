package com.example.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "defenseevalscore")
public class DefenseEvalScore {

    @Id
    @Column(name = "eval_id")
    private String evalId;

    @Column(name = "score")
    private BigDecimal score;

    // criteria_id
    @ManyToOne
    @JoinColumn(name = "criteria_id")
    @JsonManagedReference
    private Criteria criteria;

    // defense_eva_id
    @ManyToOne
    @JoinColumn(name = "defense_eva_id")
    @JsonBackReference
    private DefenseEvaluation defenseEvaluation;

    public String getEvalId() {
        return evalId;
    }

    public DefenseEvalScore setEvalId(String evalId) {
        this.evalId = evalId;
        return this;
    }

    public BigDecimal getScore() {
        return score;
    }

    public DefenseEvalScore setScore(float score) {
        this.score = BigDecimal.valueOf(score);
        return this;
    }

    public Criteria getCriteria() {
        return criteria;
    }

    public DefenseEvalScore setCriteria(Criteria criteria) {
        this.criteria = criteria;
        return this;
    }

    public DefenseEvaluation getDefenseEvaluation() {
        return defenseEvaluation;
    }

    public DefenseEvalScore setDefenseEvaluation(DefenseEvaluation defenseEvaluation) {
        this.defenseEvaluation = defenseEvaluation;
        return this;
    }

    @Override
    public String toString() {
        return "DefenseEvalScore{" +
                "evalId='" + evalId + '\'' +
                ", score=" + score +
                '}';
    }
}
