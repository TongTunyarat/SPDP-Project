package com.example.project.DTO.ManageSchedule;

public class TimeSlotDTO {
    private String startTime;
    private String endTime;
    private boolean isBreak;
    private String breakLabel;

    public TimeSlotDTO(String startTime, String endTime, boolean isBreak, String breakLabel) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.isBreak = isBreak;
        this.breakLabel = breakLabel;
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

    public boolean isBreak() {
        return isBreak;
    }

    public void setBreak(boolean aBreak) {
        isBreak = aBreak;
    }

    public String getBreakLabel() {
        return breakLabel;
    }

    public void setBreakLabel(String breakLabel) {
        this.breakLabel = breakLabel;
    }
}