package com.example.project.DTO.ManageSchedule.EditSchedule;

import java.util.List;

public class BookingSettingsProposalDTO {

    private String projectId;
    private String program;
    private String startDate;
    private String startTime;
    private String endTime;
    private String room;

    public BookingSettingsProposalDTO(String projectId, String program, String startDate, String startTime, String endTime, String room) {
        this.projectId = projectId;
        this.program = program;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
