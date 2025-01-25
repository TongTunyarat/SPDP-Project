package com.example.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

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

//    @OneToMany(mappedBy = "projectInstructorRole", cascade = CascadeType.ALL)
//    @JsonBackReference
//    private List<ProposalEvaluation> proposalEvaluations;


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
}