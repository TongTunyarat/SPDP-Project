package com.example.project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "Criteria")
public class Criteria {

    @Id
    @Column(name = "criteria_id")
    private String criteriaId;

    @Column(name = "criteria_name")
    private String criteriaName;

    @Column(name = "max_score")
    private String maxScore;

    @Column(name = "type")
    private String type;

    @Column(name = "weight")
    private float weight;

    public String getCriteriaId() {
        return criteriaId;
    }

    public String getCriteriaName() {
        return criteriaName;
    }

    public String getMaxScore() {
        return maxScore;
    }

    public String getType() {
        return type;
    }

    public float getWeight() {
        return weight;
    }
}
