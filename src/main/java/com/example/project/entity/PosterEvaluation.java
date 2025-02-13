package com.example.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@Table(name = "posterevaluation")
public class PosterEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "poster_id")
    private String posterId;

    @Column(name = "comment")
    private String comment;

    @Column(name = "edited_on")
    private LocalDateTime editedOn;

    @Column(name = "recorded_on")
    private LocalDateTime recordedOn;

    @Column(name = "total_score")
    private BigDecimal totalScore;

    // recorded_by
    @ManyToOne
    @JoinColumn(name = "recorded_by")
    @JsonManagedReference
    private Account recordedByPoster;

    // edited_by
    @ManyToOne
    @JoinColumn(name = "edited_by")
    @JsonManagedReference
    private Account editedByPoster;

    // project_id
    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonManagedReference
    private Project projectIdPoster;

    // instructor_id
    @ManyToOne
    @JoinColumn(name = "instructor_id")
    @JsonManagedReference
    private ProjectInstructorRole instructorIdPoster;

    // map to poster eva score
    @OneToMany(mappedBy = "posterEvaluation", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<PosterEvaluationScore> posterEvaluationScores;

    public String getPosterId() {
        return posterId;
    }

    public PosterEvaluation setPosterId(String posterId) {
        this.posterId = posterId;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public PosterEvaluation setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public LocalDateTime getEditedOn() {
        return editedOn;
    }

    public PosterEvaluation setEditedOn(LocalDateTime editedOn) {
        this.editedOn = editedOn;
        return this;
    }

    public LocalDateTime getRecordedOn() {
        return recordedOn;
    }

    public PosterEvaluation setRecordedOn(LocalDateTime recordedOn) {
        this.recordedOn = recordedOn;
        return this;
    }

    public Account getRecordedByPoster() {
        return recordedByPoster;
    }

    public PosterEvaluation setRecordedByPoster(Account recordedByPoster) {
        this.recordedByPoster = recordedByPoster;
        return this;
    }

    public Account getEditedByPoster() {
        return editedByPoster;
    }

    public PosterEvaluation setEditedByPoster(Account editedByPoster) {
        this.editedByPoster = editedByPoster;
        return this;
    }

    public Project getProjectIdPoster() {
        return projectIdPoster;
    }

    public PosterEvaluation setProjectIdPoster(Project projectIdPoster) {
        this.projectIdPoster = projectIdPoster;
        return this;
    }

    public ProjectInstructorRole getInstructorIdPoster() {
        return instructorIdPoster;
    }

    public PosterEvaluation setInstructorIdPoster(ProjectInstructorRole instructorIdPoster) {
        this.instructorIdPoster = instructorIdPoster;
        return this;
    }

    public List<PosterEvaluationScore> getPosterEvaluationScores() {
        return posterEvaluationScores;
    }

    public PosterEvaluation setPosterEvaluationScores(List<PosterEvaluationScore> posterEvaluationScores) {
        this.posterEvaluationScores = posterEvaluationScores;
        return this;
    }

    public BigDecimal getTotalScore() {
        return totalScore;
    }

    public PosterEvaluation setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
        return this;
    }

}
