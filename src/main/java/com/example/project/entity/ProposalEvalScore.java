package com.example.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.util.Optional;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "proposalevalscore")
public class ProposalEvalScore {

    @Id
    @Column(name = "eva_id")
    private String evaId;

    @Column(name = "score")
    private BigDecimal score;

    // map to criteria
    @ManyToOne
    @JoinColumn(name = "criteria_id")
    @JsonManagedReference
    private Criteria criteria;

    // map to proposalEvaluation
    @ManyToOne
    @JoinColumn(name = "proposal_id")
    @JsonBackReference
    private ProposalEvaluation proposalEvaluation;

    public static Optional<Object> stream() {
        return null;
    }

    public String getEvaId() {
        return evaId;
    }

    public ProposalEvalScore setEvaId(String evaId) {
        this.evaId = evaId;
        return this;
    }

    public BigDecimal getScore() {
        return score;
    }

    public ProposalEvalScore setScore(BigDecimal score) {
        this.score = score;
        return this;
    }

    public Criteria getCriteria() {
        return criteria;
    }

    public ProposalEvalScore setCriteria(Criteria criteria) {
        this.criteria = criteria;
        return this;
    }

    public ProposalEvaluation getProposalEvaluation() {
        return proposalEvaluation;
    }

    public ProposalEvalScore setProposalEvaluation(ProposalEvaluation proposalEvaluation) {
        this.proposalEvaluation = proposalEvaluation;
        return this;
    }

    @Override
    public String toString() {
        return "ProposalEvalScore{" +
                "evaId='" + evaId + '\'' +
                ", score=" + score +
                '}';
    }
}
