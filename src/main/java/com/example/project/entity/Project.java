package com.example.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.*;
import lombok.Data;
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

    // TODO: ทำเพิ่ม
    // map for proposal evaluation
    // map for poster evaluation
    // map for proposal grading
    // มี map ของตารางอื่นอีกด้วยต้องเช็คเพิ่ม



//    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
//    @JsonBackReference
//    private List<ProjectInstructorRole> projectInstructorRoles;

//    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
//    @JsonBackReference
//    private List<StudentProject> studentProjects;

//    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
//    @JsonBackReference
//    private List<GradingProposalEvaluation> gradingProposalEvaluations;

//    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
//    @JsonBackReference
//    private List<ProposalEvaluation> proposalEvaluations;


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
}

