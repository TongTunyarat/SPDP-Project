package com.example.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "defenseevalscore")
public class DefenseEvalScore {

    @Id
    @Column(name = "eval_id")
    private String evalId;

    @Column(name = "score")
    private int score;

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

    public void setEvalId(String evalId) {
        this.evalId = evalId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Criteria getCriteria() {
        return criteria;
    }

    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    public DefenseEvaluation getDefenseEvaluation() {
        return defenseEvaluation;
    }

    public void setDefenseEvaluation(DefenseEvaluation defenseEvaluation) {
        this.defenseEvaluation = defenseEvaluation;
    }
}
