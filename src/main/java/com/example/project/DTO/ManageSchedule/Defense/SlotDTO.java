package com.example.project.DTO.ManageSchedule.Defense;

import java.time.LocalDateTime;

public class SlotDTO {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String room;

    public SlotDTO(LocalDateTime startTime, LocalDateTime endTime, String room) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
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
}
