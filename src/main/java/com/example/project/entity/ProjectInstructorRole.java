package com.example.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name="projectinstructorrole")
public class ProjectInstructorRole {

    @Id
    @Column(name = "instructor_id")
    private String instructorId;

    @Column(name = "assign_date")
    private LocalDateTime assignDate;

    @Column(name = "role")
    private String role;

    // project_id
    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonManagedReference
    private Project projectIdRole;

    // professor_id
    @ManyToOne
    @JoinColumn(name = "professor_id")
    @JsonManagedReference
    private Instructor instructor;

    // map to defense eva
    @OneToMany(mappedBy = "defenseInstructorId", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<DefenseEvaluation> defenseEvaluations;

    // map to poster eva
    @OneToMany(mappedBy = "instructorIdPoster", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<PosterEvaluation> posterEvaluations;


    @OneToMany(mappedBy = "projectInstructorRole", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<ProposalEvaluation> proposalEvaluations;

    public ProjectInstructorRole(Project project) {
    }

    public ProjectInstructorRole() {

    }


    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

    public LocalDateTime getAssignDate() {
        return assignDate;
    }

    public void setAssignDate(LocalDateTime assignDate) {
        this.assignDate = assignDate;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Project getProjectIdRole() {
        return projectIdRole;
    }

    public void setProjectIdRole(Project projectIdRole) {
        this.projectIdRole = projectIdRole;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public List<DefenseEvaluation> getDefenseEvaluations() {
        return defenseEvaluations;
    }

    public void setDefenseEvaluations(List<DefenseEvaluation> defenseEvaluations) {
        this.defenseEvaluations = defenseEvaluations;
    }

    public List<PosterEvaluation> getPosterEvaluations() {
        return posterEvaluations;
    }

    public void setPosterEvaluations(List<PosterEvaluation> posterEvaluations) {
        this.posterEvaluations = posterEvaluations;
    }

    public List<ProposalEvaluation> getProposalEvaluations() {
        return proposalEvaluations;
    }

    public void setProposalEvaluations(List<ProposalEvaluation> proposalEvaluations) {
        this.proposalEvaluations = proposalEvaluations;
    }


    public void setProjectInstructorRoleId(String instructorRoleId) {

    }

    @Override
    public String toString() {
        return "ProjectInstructorRole{" +
                "instructorId='" + instructorId + '\'' +
                ", assignDate=" + assignDate +
                ", role='" + role + '\'' +
                ", projectId=" + (projectIdRole != null ? projectIdRole.getProjectId() : null) +
                ", professorId=" + (instructor != null ? instructor.getProfessorId() : null) +
                '}';
    }
}