package com.example.project.DTO.Dashboard;

import com.example.project.entity.*;

import java.util.List;

public class ProposalGradeDTO {
    private List<StudentProject> studentProject;
    private List<GradingProposalEvaluation> gradingProposalEvaluation;
    private List<ProjectInstructorRole> projectInstructorRole;

    public ProposalGradeDTO() {
    }

    public ProposalGradeDTO(List<StudentProject> studentProject, List<GradingProposalEvaluation> gradingProposalEvaluation, List<ProjectInstructorRole> projectInstructorRole) {
        this.studentProject = studentProject;
        this.gradingProposalEvaluation = gradingProposalEvaluation;
        this.projectInstructorRole = projectInstructorRole;
    }

    public List<StudentProject> getStudentProject() {
        return studentProject;
    }

    public void setStudentProject(List<StudentProject> studentProject) {
        this.studentProject = studentProject;
    }

    public List<GradingProposalEvaluation> getGradingProposalEvaluation() {
        return gradingProposalEvaluation;
    }

    public void setGradingProposalEvaluation(List<GradingProposalEvaluation> gradingProposalEvaluation) {
        this.gradingProposalEvaluation = gradingProposalEvaluation;
    }

    public List<ProjectInstructorRole> getProjectInstructorRole() {
        return projectInstructorRole;
    }

    public void setProjectInstructorRole(List<ProjectInstructorRole> projectInstructorRole) {
        this.projectInstructorRole = projectInstructorRole;
    }
}
