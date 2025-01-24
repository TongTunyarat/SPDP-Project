package com.example.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Entity
@Table(name = "Account")
public class Account {

    @Id
    @Column(name = "user_username")
    private String username;

    @Column(name = "user_password")
    private String password;

    // https://www.baeldung.com/jpa-one-to-one
    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    @JsonBackReference
    private Instructor instructors;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<ProposalEvaluation> proposalEvaluations;

    @OneToMany(mappedBy = "accountEdit", cascade = CascadeType.ALL)
    @JsonBackReference
    private  List<ProposalEvaluation> proposalEvaluationsEdit;

    public Account() {

    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Instructor getInstructors() {
        return instructors;
    }

    public void setInstructors(Instructor instructors) {
        this.instructors = instructors;
    }

    public List<ProposalEvaluation> getProposalEvaluations() {
        return proposalEvaluations;
    }

    public void setProposalEvaluations(List<ProposalEvaluation> proposalEvaluations) {
        this.proposalEvaluations = proposalEvaluations;
    }

    public List<ProposalEvaluation> getProposalEvaluationsEdit() {
        return proposalEvaluationsEdit;
    }

    public void setProposalEvaluationsEdit(List<ProposalEvaluation> proposalEvaluationsEdit) {
        this.proposalEvaluationsEdit = proposalEvaluationsEdit;
    }
}