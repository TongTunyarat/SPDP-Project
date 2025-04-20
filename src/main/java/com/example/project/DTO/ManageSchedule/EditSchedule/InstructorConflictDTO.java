package com.example.project.DTO.ManageSchedule.EditSchedule;

import java.time.LocalTime;

public class InstructorConflictDTO {

    private String instructorName;
    private String conflictingProjectId;
    private String conflictingDate;
    private LocalTime conflictStartTime;
    private LocalTime conflictEndTime;

    public InstructorConflictDTO(String instructorName, String conflictingProjectId, LocalTime conflictStartTime, LocalTime conflictEndTime) {
        this.instructorName = instructorName;
        this.conflictingProjectId = conflictingProjectId;
        this.conflictStartTime = conflictStartTime;
        this.conflictEndTime = conflictEndTime;
    }

    public InstructorConflictDTO(String instructorName, String conflictingProjectId, String conflictingDate, LocalTime conflictStartTime, LocalTime conflictEndTime) {
        this.instructorName = instructorName;
        this.conflictingProjectId = conflictingProjectId;
        this.conflictingDate = conflictingDate;
        this.conflictStartTime = conflictStartTime;
        this.conflictEndTime = conflictEndTime;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public String getConflictingProjectId() {
        return conflictingProjectId;
    }

    public void setConflictingProjectId(String conflictingProjectId) {
        this.conflictingProjectId = conflictingProjectId;
    }

    public LocalTime getConflictStartTime() {
        return conflictStartTime;
    }

    public void setConflictStartTime(LocalTime conflictStartTime) {
        this.conflictStartTime = conflictStartTime;
    }

    public LocalTime getConflictEndTime() {
        return conflictEndTime;
    }

    public void setConflictEndTime(LocalTime conflictEndTime) {
        this.conflictEndTime = conflictEndTime;
    }

    public String getConflictingDate() {
        return conflictingDate;
    }

    public void setConflictingDate(String conflictingDate) {
        this.conflictingDate = conflictingDate;
    }
}