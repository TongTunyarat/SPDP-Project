package com.example.project.DTO.ManageSchedule.EditSchedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class GetAllEditProposalScheduleDTO {

    private String projectId;
    private String projectTitle;
    private String program;
    private String semester;
    private Map<String, List<String>> instructorNames;
    private String date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String room;
    private String status;
    private String editedByUser;

    public GetAllEditProposalScheduleDTO(String projectId, String projectTitle, String program, String semester, Map<String, List<String>> instructorNames, String date, LocalDateTime startTime, LocalDateTime endTime, String room, String status, String editedByUser) {
        this.projectId = projectId;
        this.projectTitle = projectTitle;
        this.program = program;
        this.semester = semester;
        this.instructorNames = instructorNames;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
        this.status = status;
        this.editedByUser = editedByUser;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
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

    public Map<String, List<String>> getInstructorNames() {
        return instructorNames;
    }

    public void setInstructorNames(Map<String, List<String>> instructorNames) {
        this.instructorNames = instructorNames;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }


    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEditedByUser() {
        return editedByUser;
    }

    public void setEditedByUser(String editedByUser) {
        this.editedByUser = editedByUser;
    }
}
