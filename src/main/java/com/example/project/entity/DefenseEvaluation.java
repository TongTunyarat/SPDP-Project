package com.example.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "defenseevaluation")
public class DefenseEvaluation {

    @Id
    @Column(name = "defense_eva_id")
    private String defenseEvaId;

    @Column(name = "recorded_on")
    private LocalDateTime recordedOn;

    @Column(name = "comment")
    private String comment;

    @Column(name = "edited_on")
    private LocalDateTime editedOn;

    @OneToMany(mappedBy = "defenseEvaluation", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<DefenseEvalScore> defenseEvalScore;

    // recorded_by
    @ManyToOne
    @JoinColumn(name = "recorded_by")
    @JsonManagedReference
    private Account recordedBy;

    // edited_by
    @ManyToOne
    @JoinColumn(name = "edited_by")
    @JsonManagedReference
    private Account editedBy;

    // project_id
    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonManagedReference
    private Project projectId;

    // instructor_id
    @ManyToOne
    @JoinColumn(name = "instructor_id")
    @JsonManagedReference
    private ProjectInstructorRole defenseInstructorId;

    @ManyToOne
    @JoinColumn(name="student_id")
    @JsonManagedReference
    private Student student;

//
//    @ManyToOne
//    @JoinColumn(name = "student_id", referencedColumnName = "student_id")
//    private Student studentId;


    public String getDefenseEvaId() {
        return defenseEvaId;
    }

    public void setDefenseEvaId(String defenseEvaId) {
        this.defenseEvaId = defenseEvaId;
    }

    public LocalDateTime getRecordedOn() {
        return recordedOn;
    }

    public void setRecordedOn(LocalDateTime recordedOn) {
        this.recordedOn = recordedOn;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getEditedOn() {
        return editedOn;
    }

    public void setEditedOn(LocalDateTime editedOn) {
        this.editedOn = editedOn;
    }

    public Account getRecordedBy() {
        return recordedBy;
    }

    public void setRecordedBy(Account recordedBy) {
        this.recordedBy = recordedBy;
    }

    public Account getEditedBy() {
        return editedBy;
    }

    public void setEditedBy(Account editedBy) {
        this.editedBy = editedBy;
    }

    public List<DefenseEvalScore> getDefenseEvalScore() {
        return defenseEvalScore;
    }

    public void setDefenseEvalScore(List<DefenseEvalScore> defenseEvalScore) {
        this.defenseEvalScore = defenseEvalScore;
    }

    public Project getProjectId() {
        return projectId;
    }

    public void setProjectId(Project projectId) {
        this.projectId = projectId;
    }

    public ProjectInstructorRole getDefenseInstructorId() {
        return defenseInstructorId;
    }

    public void setDefenseInstructorId(ProjectInstructorRole defenseInstructorId) {
        this.defenseInstructorId = defenseInstructorId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
