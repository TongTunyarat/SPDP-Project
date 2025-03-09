package com.example.project.DTO.Dashboard;

import com.example.project.entity.GradingDefenseEvaluation;
import com.example.project.entity.GradingProposalEvaluation;
import com.example.project.entity.ProjectInstructorRole;
import com.example.project.entity.StudentProject;

import java.util.List;

public class DefenseGradeDTO {
    private List<StudentProject> studentProject;
    private List<GradingDefenseEvaluation> gradingDefenseEvaluation;
    private List<ProjectInstructorRole> projectInstructorRole;

    public DefenseGradeDTO() {
    }

    public DefenseGradeDTO(List<StudentProject> studentProject, List<GradingDefenseEvaluation> gradingDefenseEvaluation, List<ProjectInstructorRole> projectInstructorRole) {
        this.studentProject = studentProject;
        this.gradingDefenseEvaluation = gradingDefenseEvaluation;
        this.projectInstructorRole = projectInstructorRole;
    }

    public List<StudentProject> getStudentProject() {
        return studentProject;
    }

    public void setStudentProject(List<StudentProject> studentProject) {
        this.studentProject = studentProject;
    }

    public List<GradingDefenseEvaluation> getGradingDefenseEvaluation() {
        return gradingDefenseEvaluation;
    }

    public void setGradingDefenseEvaluation(List<GradingDefenseEvaluation> gradingDefenseEvaluation) {
        this.gradingDefenseEvaluation = gradingDefenseEvaluation;
    }

    public List<ProjectInstructorRole> getProjectInstructorRole() {
        return projectInstructorRole;
    }

    public void setProjectInstructorRole(List<ProjectInstructorRole> projectInstructorRole) {
        this.projectInstructorRole = projectInstructorRole;
    }
}
