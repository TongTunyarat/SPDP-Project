package com.example.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
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

    // recorded_by
    @ManyToOne
    @JoinColumn(name = "recorded_by")
    @JsonManagedReference
    private Account recordedProject;

    // edited_by
    @ManyToOne
    @JoinColumn(name = "edited_by")
    @JsonManagedReference
    private Account editedProject;

    // map for defense evaluation
    @OneToMany(mappedBy = "projectId", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<DefenseEvaluation> defenseEvaluations;

    // map for defense grading
    @OneToMany(mappedBy = "projectId", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<GradingDefenseEvaluation> gradingDefenseEvaluations;

    // map for projectInstructorRole
    @OneToMany(mappedBy = "projectIdRole", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<ProjectInstructorRole> projectInstructorRoles;

    // map for poster evaluation
    @OneToMany(mappedBy = "projectIdPoster", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<PosterEvaluation> posterEvaluations;

//    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
//    @JsonBackReference
//    private List<ProjectInstructorRole> projectInstructorRoles;

    // map for studentProjects
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<StudentProject> studentProjects;

    // map for gradingProposalEvaluations
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<GradingProposalEvaluation> gradingProposalEvaluations;

    // map for proposalEvaluations
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<ProposalEvaluation> proposalEvaluations;


    public Project() {

    }

    // Constructor with arguments
    public Project(String projectId, String program, String semester, String projectTitle,
                   String projectCategory, String projectDescription,
                   LocalDateTime recordedOn, LocalDateTime editedOn,
                   Account recordedProject, Account editedProject) {
        this.projectId = projectId;
        this.program = program;
        this.semester = semester;
        this.projectTitle = projectTitle;
        this.projectCategory = projectCategory;
        this.projectDescription = projectDescription;
        this.recordedOn = recordedOn;
        this.editedOn = editedOn;
        this.recordedProject = recordedProject;
        this.editedProject = editedProject;
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

    public Account getRecordedProject() {
        return recordedProject;
    }

    public void setRecordedProject(Account recordedProject) {
        this.recordedProject = recordedProject;
    }

    public Account getEditedProject() {
        return editedProject;
    }

    public void setEditedProject(Account editedProject) {
        this.editedProject = editedProject;
    }

    public List<DefenseEvaluation> getDefenseEvaluations() {
        return defenseEvaluations;
    }

    public void setDefenseEvaluations(List<DefenseEvaluation> defenseEvaluations) {
        this.defenseEvaluations = defenseEvaluations;
    }

    public List<GradingDefenseEvaluation> getGradingDefenseEvaluations() {
        return gradingDefenseEvaluations;
    }

    public void setGradingDefenseEvaluations(List<GradingDefenseEvaluation> gradingDefenseEvaluations) {
        this.gradingDefenseEvaluations = gradingDefenseEvaluations;
    }

    public List<ProjectInstructorRole> getProjectInstructorRoles() {
        return projectInstructorRoles;
    }

    public void setProjectInstructorRoles(List<ProjectInstructorRole> projectInstructorRoles) {
        this.projectInstructorRoles = projectInstructorRoles;
    }

    public List<PosterEvaluation> getPosterEvaluations() {
        return posterEvaluations;
    }

    public void setPosterEvaluations(List<PosterEvaluation> posterEvaluations) {
        this.posterEvaluations = posterEvaluations;
    }

    public List<StudentProject> getStudentProjects() {
        return studentProjects;
    }

    public void setStudentProjects(List<StudentProject> studentProjects) {
        this.studentProjects = studentProjects;
    }

    public List<ProposalEvaluation> getProposalEvaluations() {
        return proposalEvaluations;
    }

    public void setProposalEvaluations(List<ProposalEvaluation> proposalEvaluations) {
        this.proposalEvaluations = proposalEvaluations;
    }

    public List<GradingProposalEvaluation> getGradingProposalEvaluations() {
        return gradingProposalEvaluations;
    }

    public void setGradingProposalEvaluations(List<GradingProposalEvaluation> gradingProposalEvaluations) {
        this.gradingProposalEvaluations = gradingProposalEvaluations;
    }

    @Override
    public String toString() {
        return "Project{" +
                "projectId='" + projectId + '\'' +
                ", program='" + program + '\'' +
                ", semester='" + semester + '\'' +
                ", projectTitle='" + projectTitle + '\'' +
                ", projectCategory='" + projectCategory + '\'' +
                ", projectDescription='" + projectDescription + '\'' +
                ", recordedOn=" + recordedOn +
                ", editedOn=" + editedOn +
                ", recordedBy=" + (recordedProject != null ? recordedProject.getUsername() : null) +
                ", editedBy=" + (editedProject != null ? editedProject.getUsername() : null) +
                '}';
    }

    public Collection<Student> getStudentList() {
        return List.of();
    }

    public void setStudentList(ArrayList<Object> objects) {
    }
}
