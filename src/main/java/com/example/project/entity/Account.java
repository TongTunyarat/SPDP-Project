package com.example.project.entity;

// <<<<<<< Nref
import com.fasterxml.jackson.annotation.JsonBackReference;
// =======
// >>>>>>> Tong
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

// <<<<<<< Nref
import java.util.List;

// =======
// >>>>>>> Tong
@Getter
@Entity
@Table(name = "Account")
public class Account {

    @Id
    @Column(name = "user_username")
    private String username;

    @Column(name = "user_password")
    private String password;

// <<<<<<< Nref
//     // https://www.baeldung.com/jpa-one-to-one
//     @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
//     @JsonBackReference
//     private Instructor instructors;

//     @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
//     @JsonBackReference
//     private List<ProposalEvaluation> proposalEvaluations;

//     @OneToMany(mappedBy = "accountEdit", cascade = CascadeType.ALL)
//     @JsonBackReference
//     private  List<ProposalEvaluation> proposalEvaluationsEdit;

//     public Account() {

//     }


// =======
// >>>>>>> Tong
//     public String getUsername() {
//         return username;
//     }

// <<<<<<< Nref
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
// =======
//     public String getPassword() {
//         return password;
//     }
// }

// >>>>>>> Tong
