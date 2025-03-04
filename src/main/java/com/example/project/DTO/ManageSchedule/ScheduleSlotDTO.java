package com.example.project.DTO.ManageSchedule;

import java.time.LocalDateTime;

public class ScheduleSlotDTO {

    private final String room;
    private final LocalDateTime startTime;


    public ScheduleSlotDTO(String room, LocalDateTime startTime) {
        this.room = room;
        this.startTime = startTime;
    }

    public String getRoom() {
        return room;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public String toString() {
        return "ScheduleSlotDTO{" +
                "room='" + room + '\'' +
                ", startTime=" + startTime +
                '}';
    }
}
