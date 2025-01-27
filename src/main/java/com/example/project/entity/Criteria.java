package com.example.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
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

    // map to defense eva score
    @OneToMany(mappedBy = "criteria", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<DefenseEvalScore> defenseEvalScore;


    // TODO: ทำเพิ่ม
    // map to poster eva score

    // map to proposal eva score
    @OneToMany(mappedBy = "criteria", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<ProposalEvalScore> proposalEvalScores;

    public String getCriteriaId() {
        return criteriaId;
    }

    public void setCriteriaId(String criteriaId) {
        this.criteriaId = criteriaId;
    }

    public String getCriteriaName() {
        return criteriaName;
    }

    public void setCriteriaName(String criteriaName) {
        this.criteriaName = criteriaName;
    }

    public String getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(String maxScore) {
        this.maxScore = maxScore;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public List<DefenseEvalScore> getDefenseEvalScore() {
        return defenseEvalScore;
    }

    public void setDefenseEvalScore(List<DefenseEvalScore> defenseEvalScore) {
        this.defenseEvalScore = defenseEvalScore;
    }

    public List<ProposalEvalScore> getProposalEvalScores() {
        return proposalEvalScores;
    }

    public void setProposalEvalScores(List<ProposalEvalScore> proposalEvalScores) {
        this.proposalEvalScores = proposalEvalScores;
    }
}