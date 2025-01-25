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
// <<<<<<< Nref
import java.util.List;
// =======
// >>>>>>> Tong

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
// <<<<<<< Nref
    @JsonBackReference
=======
// >>>>>>> Tong
    @JoinColumn(name = "recorded_by", referencedColumnName = "user_username")
    private Account recordedBy;

    @ManyToOne
// <<<<<<< Nref
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

// =======
    @JoinColumn(name = "edited_by", referencedColumnName = "user_username")
    private Account editedBy;

//     Getters สำหรับทุกฟิลด์
// >>>>>>> Tong
    public String getProjectId() {
        return projectId;
    }

// <<<<<<< Nref
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

// =======
// >>>>>>> Tong
    public String getProgram() {
        return program;
    }

// <<<<<<< Nref
    public void setProgram(String program) {
        this.program = program;
    }

// =======
// >>>>>>> Tong
    public String getSemester() {
        return semester;
    }

// <<<<<<< Nref
    public void setSemester(String semester) {
        this.semester = semester;
    }

// =======
// >>>>>>> Tong
    public String getProjectTitle() {
        return projectTitle;
    }

// <<<<<<< Nref
    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

// =======
// >>>>>>> Tong
    public String getProjectCategory() {
        return projectCategory;
    }

// <<<<<<< Nref
    public void setProjectCategory(String projectCategory) {
        this.projectCategory = projectCategory;
    }

// =======
// >>>>>>> Tong
    public String getProjectDescription() {
        return projectDescription;
    }

// <<<<<<< Nref
    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

// =======
// >>>>>>> Tong
    public LocalDateTime getRecordedOn() {
        return recordedOn;
    }

// <<<<<<< Nref
    public void setRecordedOn(LocalDateTime recordedOn) {
        this.recordedOn = recordedOn;
    }

// =======
// >>>>>>> Tong
    public LocalDateTime getEditedOn() {
        return editedOn;
    }

// <<<<<<< Nref
    public void setEditedOn(LocalDateTime editedOn) {
        this.editedOn = editedOn;
    }

// =======
// >>>>>>> Tong
    public Account getRecordedBy() {
        return recordedBy;
    }

// <<<<<<< Nref
    public void setRecordedBy(Account recordedBy) {
        this.recordedBy = recordedBy;
    }

// =======
// >>>>>>> Tong
    public Account getEditedBy() {
        return editedBy;
    }

// <<<<<<< Nref
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
// =======
// }


// >>>>>>> Tong
