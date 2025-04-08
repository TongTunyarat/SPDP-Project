package com.example.project.DTO.ManageSchedule.EditSchedule;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class RoomColflictDTO {
    private String date;
    private String room;
    private String projectId;
    private LocalTime conflictStartTime;
    private LocalTime conflictEndTime;

    public RoomColflictDTO(String date, String room, String projectId, LocalTime conflictStartTime, LocalTime conflictEndTime) {
        this.date = date;
        this.room = room;
        this.projectId = projectId;
        this.conflictStartTime = conflictStartTime;
        this.conflictEndTime = conflictEndTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
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

    @Override
    public String toString() {
        return "RoomColflictDTO{" +
                "date='" + date + '\'' +
                ", room='" + room + '\'' +
                ", projectId='" + projectId + '\'' +
                ", conflictStartTime=" + conflictStartTime +
                ", conflictEndTime=" + conflictEndTime +
                '}';
    }
}
