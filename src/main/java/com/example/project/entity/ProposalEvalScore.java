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

//    @Column(name = "criteria_id")
//    private String criteriaId;

    // map to criteria
    @ManyToOne
    @JoinColumn(name = "criteria_id")
    @JsonManagedReference
    private Criteria criteria;

    // map to proposalEvaluation
    @ManyToOne
    @JoinColumn(name = "proposal_id")
    @JsonManagedReference
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

    public ProposalEvaluation getProposalEvaluation() {
        return proposalEvaluation;
    }

    public void setProposalEvaluation(ProposalEvaluation proposalEvaluation) {
        this.proposalEvaluation = proposalEvaluation;
    }

    public Criteria getCriteria() {
        return criteria;
    }

    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }
}
