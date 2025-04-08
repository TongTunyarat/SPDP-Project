package com.example.project.DTO.Dashboard;

public class TeacherScoringDTO {
    private String instructorName;
    private String instructorId;
    private int totalAssigned;
    private int completedCount;
    private int remainingCount;
    private double completionPercentage;

    public TeacherScoringDTO() {
    }

    public String getInstructorName() {
        return instructorName;
    }

    public TeacherScoringDTO(String instructorName, String instructorId, int totalAssigned, int completedCount, int remainingCount, double completionPercentage) {
        this.instructorName = instructorName;
        this.instructorId = instructorId;
        this.totalAssigned = totalAssigned;
        this.completedCount = completedCount;
        this.remainingCount = remainingCount;
        this.completionPercentage = completionPercentage;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

    public int getTotalAssigned() {
        return totalAssigned;
    }

    public void setTotalAssigned(int totalAssigned) {
        this.totalAssigned = totalAssigned;
    }

    public int getCompletedCount() {
        return completedCount;
    }

    public void setCompletedCount(int completedCount) {
        this.completedCount = completedCount;
    }

    public int getRemainingCount() {
        return remainingCount;
    }

    public void setRemainingCount(int remainingCount) {
        this.remainingCount = remainingCount;
    }

    public double getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(double completionPercentage) {
        this.completionPercentage = completionPercentage;
    }
}
