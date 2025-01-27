package com.example.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="proposalevaluation")
public class ProposalEvaluation {

    @Id
    @Column(name="proposal_id")
    private String proposalId;

    @Column(name="comment")
    private String comment;

    @Column(name="recorded_on")
    private LocalDateTime recordedOn;

    @Column(name="edited_on")
    private LocalDateTime editedOn;

    @ManyToOne
    @JoinColumn(name = "recorded_by")
    @JsonManagedReference
    private Account account;

    @ManyToOne
    @JoinColumn(name = "edited_by")
    @JsonManagedReference
    private Account accountEdit;

//    @Column(name="recorded_by")
//    private String recordedBy;
//
//    @Column(name="edited_by")
//    private String editedBy;

    @ManyToOne
    @JoinColumn(name = "instructor_id")
    @JsonManagedReference
    private ProjectInstructorRole projectInstructorRole;

//    @Column(name="instructor_id")
//    private String instructorId;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonManagedReference
    private Project project;

//    @Column(name="project_id")
//    private String projectId;

//    @Column(name="student_id")
//    private String studentId;

    @ManyToOne
    @JoinColumn(name="student_id")
    @JsonManagedReference
    private Student student;

    @OneToMany(mappedBy = "proposalEvaluation", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<ProposalEvalScore> proposalEvalScores;

    public ProposalEvaluation() {

    }

    public String getProposalId() {
        return proposalId;
    }

    public void setProposalId(String proposalId) {
        this.proposalId = proposalId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getRecordedOn() {
        return recordedOn;
    }

    public void setRecordedOn(LocalDateTime recordedOn) {
        this.recordedOn = recordedOn;
    }

    public LocalDateTime getEditedOn() {
        return editedOn;
    }

    public void setEditedOn(LocalDateTime editedOn) {
        this.editedOn = editedOn;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Account getAccountEdit() {
        return accountEdit;
    }

    public void setAccountEdit(Account accountEdit) {
        this.accountEdit = accountEdit;
    }

    public ProjectInstructorRole getProjectInstructorRole() {
        return projectInstructorRole;
    }

    public void setProjectInstructorRole(ProjectInstructorRole projectInstructorRole) {
        this.projectInstructorRole = projectInstructorRole;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public List<ProposalEvalScore> getProposalEvalScores() {
        return proposalEvalScores;
    }

    public void setProposalEvalScores(List<ProposalEvalScore> proposalEvalScores) {
        this.proposalEvalScores = proposalEvalScores;
    }


}
