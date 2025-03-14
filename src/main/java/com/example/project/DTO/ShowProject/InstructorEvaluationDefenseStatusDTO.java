package com.example.project.DTO.ShowProject;

public class InstructorEvaluationDefenseStatusDTO {

    private long totalInstructors;
    private long completedInstructors;

    public InstructorEvaluationDefenseStatusDTO(long totalInstructors, long completedInstructors) {
        this.totalInstructors = totalInstructors;
        this.completedInstructors = completedInstructors;
    }

    public long getTotalInstructors() {
        return totalInstructors;
    }

    public void setTotalInstructors(long totalInstructors) {
        this.totalInstructors = totalInstructors;
    }

    public long getCompletedInstructors() {
        return completedInstructors;
    }

    public void setCompletedInstructors(long completedInstructors) {
        this.completedInstructors = completedInstructors;
    }
}
