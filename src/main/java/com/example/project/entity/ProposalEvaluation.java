package com.example.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Accessors(chain = true)
@Entity
@Table(name="proposalevaluation")
public class ProposalEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="proposal_id")
    private String proposalId;

    @Column(name="comment")
    private String comment;

    @Column(name="recorded_on")
    private LocalDateTime recordedOn;

    @Column(name="edited_on")
    private LocalDateTime editedOn;

    @Column(name="total_score")
    private BigDecimal totalScore;

    @ManyToOne
    @JoinColumn(name = "recorded_by")
    @JsonManagedReference
    private Account account;

    @ManyToOne
    @JoinColumn(name = "edited_by")
    @JsonManagedReference
    private Account accountEdit;

    @ManyToOne
    @JoinColumn(name = "instructor_id")
    @JsonManagedReference
    private ProjectInstructorRole projectInstructorRole;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonManagedReference
    private Project project;

    @ManyToOne
    @JoinColumn(name="student_id")
    @JsonManagedReference
    private Student student;

    @OneToMany(mappedBy = "proposalEvaluation", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ProposalEvalScore> proposalEvalScores;


    public String getProposalId() {
        return proposalId;
    }

    public ProposalEvaluation setProposalId(String proposalId) {
        this.proposalId = proposalId;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public ProposalEvaluation setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public LocalDateTime getRecordedOn() {
        return recordedOn;
    }

    public ProposalEvaluation setRecordedOn(LocalDateTime recordedOn) {
        this.recordedOn = recordedOn;
        return this;
    }

    public LocalDateTime getEditedOn() {
        return editedOn;
    }

    public ProposalEvaluation setEditedOn(LocalDateTime editedOn) {
        this.editedOn = editedOn;
        return this;
    }

    public Account getAccount() {
        return account;
    }

    public ProposalEvaluation setAccount(Account account) {
        this.account = account;
        return this;
    }

    public Account getAccountEdit() {
        return accountEdit;
    }

    public ProposalEvaluation setAccountEdit(Account accountEdit) {
        this.accountEdit = accountEdit;
        return this;
    }

    public ProjectInstructorRole getProjectInstructorRole() {
        return projectInstructorRole;
    }

    public ProposalEvaluation setProjectInstructorRole(ProjectInstructorRole projectInstructorRole) {
        this.projectInstructorRole = projectInstructorRole;
        return this;
    }

    public Project getProject() {
        return project;
    }

    public ProposalEvaluation setProject(Project project) {
        this.project = project;
        return this;
    }

    public Student getStudent() {
        return student;
    }

    public ProposalEvaluation setStudent(Student student) {
        this.student = student;
        return this;
    }

    public List<ProposalEvalScore> getProposalEvalScores() {
        return proposalEvalScores;
    }

    public ProposalEvaluation setProposalEvalScores(List<ProposalEvalScore> proposalEvalScores) {
        this.proposalEvalScores = proposalEvalScores;
        return this;
    }

    public BigDecimal getTotalScore() {
        return totalScore;
    }

    public ProposalEvaluation setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
        return this;
    }
}
