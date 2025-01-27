package com.example.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;;

@Data
@Entity
@Table(name = "Account")
public class Account {

    @Id
    @Column(name = "user_username")
    private String username;

    @Column(name = "user_password")
    private String password;

    // https://www.baeldung.com/jpa-one-to-one
    // map to instructors ('user_username')
    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    @JsonBackReference
    private Instructor instructors;

    // map to admin ('user_username')
    @OneToOne(mappedBy = "adminsAccount", cascade = CascadeType.ALL)
    @JsonBackReference
    private Admin admins;

    // map to project ('recorded_by')
    @OneToMany(mappedBy = "recordedProject", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Project> recordProjects;

    // map to project ('edited_by')
    @OneToMany(mappedBy = "editedProject", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Project> editProject;

    // map to defenseEvaluation ('recorded_by')
    @OneToMany(mappedBy = "recordedBy", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<DefenseEvaluation> defenseRecordedBy;

    // map to defenseEvaluation ('edited_by')
    @OneToMany(mappedBy = "editedBy", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<DefenseEvaluation> defenseEditedBy;

    // map to ScoringPeriods ('recorded_by')
    @OneToMany(mappedBy = "recordedByPeriod", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<ScoringPeriods> scoringPeriods;



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

    public Admin getAdmins() {
        return admins;
    }

    public void setAdmins(Admin admins) {
        this.admins = admins;
    }

    public List<DefenseEvaluation> getDefenseRecordedBy() {
        return defenseRecordedBy;
    }

    public void setDefenseRecordedBy(List<DefenseEvaluation> defenseRecordedBy) { this.defenseRecordedBy = defenseRecordedBy; }

    public List<DefenseEvaluation> getDefenseEditedBy() {
        return defenseEditedBy;
    }

    public void setDefenseEditedBy(List<DefenseEvaluation> defenseEditedBy) {
        this.defenseEditedBy = defenseEditedBy;
    }

    public List<Project> getRecordProjects() {
        return recordProjects;
    }

    public void setRecordProjects(List<Project> recordProjects) {
        this.recordProjects = recordProjects;
    }

    public List<Project> getEditProject() {
        return editProject;
    }

    public void setEditProject(List<Project> editProject) { this.editProject = editProject; }

    public List<ScoringPeriods> getScoringPeriods() { return scoringPeriods; }

    public void setScoringPeriods(List<ScoringPeriods> scoringPeriods) { this.scoringPeriods = scoringPeriods; }
}
