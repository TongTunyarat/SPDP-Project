package com.example.project.DTO.ManageSchedule;

import java.time.LocalDateTime;

public class ScheduleSlotDTO {

    private final String room;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public ScheduleSlotDTO(String room, LocalDateTime startTime, LocalDateTime endTime) {
        this.room = room;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getRoom() {
        return room;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "ScheduleSlotDTO{" +
                "room='" + room + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
