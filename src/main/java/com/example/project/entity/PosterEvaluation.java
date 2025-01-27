package com.example.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@Table(name = "posterevaluation")
public class PosterEvaluation {

    @Id
    @Column(name = "poster_id")
    private String posterId;

    @Column(name = "comment")
    private String comment;

    @Column(name = "edited_on")
    private LocalDateTime editedOn;

    @Column(name = "recorded_on")
    private LocalDateTime recordedOn;

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

    public void setPosterId(String posterId) {
        this.posterId = posterId;
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

    public LocalDateTime getRecordedOn() {
        return recordedOn;
    }

    public void setRecordedOn(LocalDateTime recordedOn) {
        this.recordedOn = recordedOn;
    }

    public Account getRecordedByPoster() {
        return recordedByPoster;
    }

    public void setRecordedByPoster(Account recordedByPoster) {
        this.recordedByPoster = recordedByPoster;
    }

    public Account getEditedByPoster() {
        return editedByPoster;
    }

    public void setEditedByPoster(Account editedByPoster) {
        this.editedByPoster = editedByPoster;
    }

    public Project getProjectIdPoster() {
        return projectIdPoster;
    }

    public void setProjectIdPoster(Project projectIdPoster) {
        this.projectIdPoster = projectIdPoster;
    }

    public ProjectInstructorRole getInstructorIdPoster() {
        return instructorIdPoster;
    }

    public void setInstructorIdPoster(ProjectInstructorRole instructorIdPoster) {
        this.instructorIdPoster = instructorIdPoster;
    }

    public List<PosterEvaluationScore> getPosterEvaluationScores() {
        return posterEvaluationScores;
    }

    public void setPosterEvaluationScores(List<PosterEvaluationScore> posterEvaluationScores) {
        this.posterEvaluationScores = posterEvaluationScores;
    }
}
