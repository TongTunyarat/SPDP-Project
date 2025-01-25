package com.example.project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "PosterEvaluation")
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

    @ManyToOne
    @JoinColumn(name = "recorded_by", referencedColumnName = "user_username")
    private Account recordedBy;

    @ManyToOne
    @JoinColumn(name = "edited_by", referencedColumnName = "user_username")
    private Account editedBy;

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "project_id")
    private Project projectId;

//    @ManyToOne
//    @JoinColumn(name = "instructor_id", referencedColumnName = "instructor_id")
//    private ProjectInstructorRole instructorId;

    public String getPosterId() {
        return posterId;
    }

    public String getComment() {
        return comment;
    }

    public Account getEditedBy() {
        return editedBy;
    }

    public Account getRecordedBy() {
        return recordedBy;
    }

    public LocalDateTime getEdited_on() {
        return editedOn;
    }

    public LocalDateTime getRecorded_on() {
        return recordedOn;
    }

    public Project getProjectId() {
        return projectId;
    }

//    public ProjectInstructorRole getInstructorId() {
//        return instructorId;
//    }

}
