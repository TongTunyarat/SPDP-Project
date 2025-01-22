package com.example.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Project")
public class Project {

    @Id
    @Column(name = "project_id")
    private String projectId;

    @Column(name = "program")
    private String program;

    @Column(name = "semester")
    private String semester;

    @Column(name = "project_title")
    private String projectTitle;

    @Column(name = "project_category")
    private String projectCategory;

    @Column(name = "project_description")
    private String projectDescription;

    @Column(name = "recorded_on")
    private LocalDateTime recordedOn;

    @Column(name = "edited_on")
    private LocalDateTime editedOn;

    @ManyToOne
    @JoinColumn(name = "recorded_by", referencedColumnName = "user_username")
    private Account recordedBy;

    @ManyToOne
    @JoinColumn(name = "edited_by", referencedColumnName = "user_username")
    private Account editedBy;

    // Getters สำหรับทุกฟิลด์
    public String getProjectId() {
        return projectId;
    }

    public String getProgram() {
        return program;
    }

    public String getSemester() {
        return semester;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public String getProjectCategory() {
        return projectCategory;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public LocalDateTime getRecordedOn() {
        return recordedOn;
    }

    public LocalDateTime getEditedOn() {
        return editedOn;
    }

    public Account getRecordedBy() {
        return recordedBy;
    }

    public Account getEditedBy() {
        return editedBy;
    }



    // Optionally, override the toString method for better debugging
//    @Override
//    public String toString() {
//        return "Project{" +
//                "projectId='" + projectId + '\'' +
//                ", program='" + program + '\'' +
//                ", semester='" + semester + '\'' +
//                ", projectTitle='" + projectTitle + '\'' +
//                ", projectCategory='" + projectCategory + '\'' +
//                ", projectDescription='" + projectDescription + '\'' +
//                ", recordedOn=" + recordedOn +
//                ", editedOn=" + editedOn +
//                ", recordedBy=" + (recordedBy != null ? recordedBy.getUsername() : "N/A") +
//                ", editedBy=" + (editedBy != null ? editedBy.getUsername() : "N/A") +
//                '}';
//    }
}

