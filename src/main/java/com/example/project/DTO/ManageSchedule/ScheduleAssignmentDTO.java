package com.example.project.DTO.ManageSchedule;

import com.example.project.entity.Project;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class ScheduleAssignmentDTO {

    private String projectId;
    private String roomNumber;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<String> instructorUsernames;
    public ScheduleAssignmentDTO(String projectId, String roomNumber, LocalDateTime startTime, LocalDateTime endTime, List<String> instructorUsernames) {
        this.projectId = projectId;
        this.roomNumber = roomNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.instructorUsernames = instructorUsernames;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
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

    public List<String> getInstructorUsernames() {
        return instructorUsernames;
    }

    public void setInstructorUsernames(List<String> instructorUsernames) {
        this.instructorUsernames = instructorUsernames;
    }

    @Override
    public String toString() {
        return "ScheduleAssignmentDTO{" +
                "projectId='" + projectId + '\'' +
                ", roomNumber='" + roomNumber + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", instructorUsernames=" + instructorUsernames +
                '}';
    }
}
