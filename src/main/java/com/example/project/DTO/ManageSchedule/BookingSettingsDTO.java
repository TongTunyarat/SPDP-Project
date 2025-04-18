package com.example.project.DTO.ManageSchedule;

import java.util.List;

public class BookingSettingsDTO {

    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private List<String> rooms;
    private String program;
    private String semester;

    public BookingSettingsDTO(String startDate, String endDate, String startTime, String endTime, List<String> rooms, String program, String semester) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.rooms = rooms;
        this.program = program;
        this.semester = semester;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
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

    public List<String> getRooms() {
        return rooms;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public void setRooms(List<String> rooms) {
        this.rooms = rooms;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    @Override
    public String toString() {
        return "BookingSettingsDTO{" +
                "startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", rooms=" + rooms +
                '}';
    }
}