package com.example.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.w3c.dom.Text;

import java.math.BigDecimal;

@Entity
@Table(name = "proposalevalscore")
public class ProposalEvalScore {

    @Id
    @Column(name = "eva_id")
    private String evaId;

    @Column(name = "score")
    private BigDecimal score;

    @Column(name = "criteria_id")
    private String criteriaId;

    @ManyToOne
    @JoinColumn(name = "proposal_id")
    @JsonBackReference
    private ProposalEvaluation proposalEvaluation;

//    @Column(name = "proposal_id")
//    private String proposalId;


    public ProposalEvalScore() {

    }

    public String getEvaId() {
        return evaId;
    }

    public void setEvaId(String evaId) {
        this.evaId = evaId;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public String getCriteriaId() {
        return criteriaId;
    }

    public void setCriteriaId(String criteriaId) {
        this.criteriaId = criteriaId;
    }

    public ProposalEvaluation getProposalEvaluation() {
        return proposalEvaluation;
    }

    public void setProposalEvaluation(ProposalEvaluation proposalEvaluation) {
        this.proposalEvaluation = proposalEvaluation;
    }
}
