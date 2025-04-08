package com.example.project.DTO.ManageSchedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class GetProposalScheduleDTO {

    private String proposalScheduleId;
    private LocalTime startTime;
    private LocalTime endTime;
    private String date;
    private String status;
    private String projectId;
    private String program;
    private String semester;
    private String projectTitle;
    private List<String> instructors;
    private String roomNumber;

    public GetProposalScheduleDTO(String proposalScheduleId, LocalTime startTime, LocalTime endTime, String date, String status, String projectId, String program, String semester, String projectTitle, List<String> instructors, String roomNumber) {
        this.proposalScheduleId = proposalScheduleId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.status = status;
        this.projectId = projectId;
        this.program = program;
        this.semester = semester;
        this.projectTitle = projectTitle;
        this.instructors = instructors;
        this.roomNumber = roomNumber;
    }

    public String getProposalScheduleId() {
        return proposalScheduleId;
    }

    public void setProposalScheduleId(String proposalScheduleId) {
        this.proposalScheduleId = proposalScheduleId;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public List<String> getInstructors() {
        return instructors;
    }

    public void setInstructors(List<String> instructors) {
        this.instructors = instructors;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }
}
