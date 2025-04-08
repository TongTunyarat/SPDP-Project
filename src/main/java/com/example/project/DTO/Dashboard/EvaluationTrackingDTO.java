package com.example.project.DTO.Dashboard;

import com.example.project.entity.GradingProposalEvaluation;
import com.example.project.entity.ProjectInstructorRole;
import com.example.project.entity.StudentProject;

import java.util.List;

public class EvaluationTrackingDTO {
    private List<ProjectInstructorRole> projectInstructorRole;
    private List<InstructorEvaStatus> instructorEvaStatus;

    public EvaluationTrackingDTO() {
    }

    public EvaluationTrackingDTO(List<ProjectInstructorRole> projectInstructorRole, List<InstructorEvaStatus> instructorEvaStatus) {
        this.projectInstructorRole = projectInstructorRole;
        this.instructorEvaStatus = instructorEvaStatus;
    }

    public List<ProjectInstructorRole> getProjectInstructorRole() {
        return projectInstructorRole;
    }

    public void setProjectInstructorRole(List<ProjectInstructorRole> projectInstructorRole) {
        this.projectInstructorRole = projectInstructorRole;
    }

    public List<InstructorEvaStatus> getInstructorEvaStatus() {
        return instructorEvaStatus;
    }

    public void setInstructorEvaStatus(List<InstructorEvaStatus> instructorEvaStatus) {
        this.instructorEvaStatus = instructorEvaStatus;
    }

    public static class InstructorEvaStatus {
        private String instructorId;
        private String projectId;
        private String projectEvaStatus;

        public InstructorEvaStatus(String instructorId, String projectId, String projectEvaStatus) {
            this.instructorId = instructorId;
            this.projectId = projectId;
            this.projectEvaStatus = projectEvaStatus;
        }

        public String getInstructorId() {
            return instructorId;
        }

        public void setInstructorId(String instructorId) {
            this.instructorId = instructorId;
        }

        public String getProjectId() {
            return projectId;
        }

        public void setProjectId(String projectId) {
            this.projectId = projectId;
        }

        public String getProjectEvaStatus() {
            return projectEvaStatus;
        }

        public void setProjectEvaStatus(String projectEvaStatus) {
            this.projectEvaStatus = projectEvaStatus;
        }
    }



}
