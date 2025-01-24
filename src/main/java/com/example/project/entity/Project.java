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
import java.util.List;

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
    @JsonBackReference
    @JoinColumn(name = "recorded_by", referencedColumnName = "user_username")
    private Account recordedBy;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "edited_by", referencedColumnName = "user_username")
    private Account editedBy;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<ProjectInstructorRole> projectInstructorRoles;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<StudentProject> studentProjects;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<GradingProposalEvaluation> gradingProposalEvaluations;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<ProposalEvaluation> proposalEvaluations;

    public Project() {

    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getProjectCategory() {
        return projectCategory;
    }

    public void setProjectCategory(String projectCategory) {
        this.projectCategory = projectCategory;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
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

    public List<ProjectInstructorRole> getProjectInstructorRoles() {
        return projectInstructorRoles;
    }

    public void setProjectInstructorRoles(List<ProjectInstructorRole> projectInstructorRoles) {
        this.projectInstructorRoles = projectInstructorRoles;
    }

    public List<StudentProject> getStudentProjects() {
        return studentProjects;
    }

    public void setStudentProjects(List<StudentProject> studentProjects) {
        this.studentProjects = studentProjects;
    }

    public List<GradingProposalEvaluation> getGradingProposalEvaluations() {
        return gradingProposalEvaluations;
    }

    public void setGradingProposalEvaluations(List<GradingProposalEvaluation> gradingProposalEvaluations) {
        this.gradingProposalEvaluations = gradingProposalEvaluations;
    }

    public List<ProposalEvaluation> getProposalEvaluations() {
        return proposalEvaluations;
    }

    public void setProposalEvaluations(List<ProposalEvaluation> proposalEvaluations) {
        this.proposalEvaluations = proposalEvaluations;
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