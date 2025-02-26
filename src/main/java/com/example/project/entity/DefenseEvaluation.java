package com.example.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "defenseevaluation")
public class DefenseEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "defense_eva_id")
    private String defenseEvaId;

    @Column(name = "recorded_on")
    private LocalDateTime recordedOn;

    @Column(name = "comment")
    private String comment;

    @Column(name = "edited_on")
    private LocalDateTime editedOn;

    @Column(name="total_score")
    private BigDecimal totalScore;

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

//    @ManyToOne
//    @JoinColumn(name="student_id")
//    @JsonManagedReference
//    private Student studentDefense;

    public String getDefenseEvaId() {
        return defenseEvaId;
    }

    public DefenseEvaluation setDefenseEvaId(String defenseEvaId) {

        this.defenseEvaId = defenseEvaId;
        return this;
    }

    public LocalDateTime getRecordedOn() {
        return recordedOn;
    }

    public DefenseEvaluation setRecordedOn(LocalDateTime recordedOn) {
        this.recordedOn = recordedOn;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public DefenseEvaluation setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public LocalDateTime getEditedOn() {
        return editedOn;
    }

    public DefenseEvaluation setEditedOn(LocalDateTime editedOn) {
        this.editedOn = editedOn;
        return this;
    }

    public Account getRecordedBy() {
        return recordedBy;
    }

    public DefenseEvaluation setRecordedBy(Account recordedBy) {
        this.recordedBy = recordedBy;
        return this;
    }

    public Account getEditedBy() {
        return editedBy;
    }

    public DefenseEvaluation setEditedBy(Account editedBy) {
        this.editedBy = editedBy;
        return this;
    }

    public List<DefenseEvalScore> getDefenseEvalScore() {
        return defenseEvalScore;
    }

    public DefenseEvaluation setDefenseEvalScore(List<DefenseEvalScore> defenseEvalScore) {
        this.defenseEvalScore = defenseEvalScore;
        return this;
    }

    public Project getProjectId() {
        return projectId;
    }

    public DefenseEvaluation setProjectId(Project projectId) {
        this.projectId = projectId;
        return this;
    }

    public ProjectInstructorRole getDefenseInstructorId() {
        return defenseInstructorId;
    }

    public DefenseEvaluation setDefenseInstructorId(ProjectInstructorRole defenseInstructorId) {
        this.defenseInstructorId = defenseInstructorId;
        return this;
    }

//    public Student getStudentDefense() {
//        return studentDefense;
//    }
//
//    public DefenseEvaluation setStudentDefense(Student studentDefense) {
//        this.studentDefense = studentDefense;
//        return this;
//    }

    public BigDecimal getTotalScore() {
        return totalScore;
    }

    public DefenseEvaluation setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
        return this;
    }

    public Student getStudent() {
        return student;
    }

    public DefenseEvaluation setStudent(Student student) {
        this.student = student;
        return this;
    }
}
