package com.example.project.DTO.ManageSchedule.Defense;

import java.util.List;
import java.util.Map;

public class BookingDefenseSettingsDTO {

    private String startDate;
    private String endDate;
    private String semesterYear;
    private Map<String, List<TimeEachSlotDTO>> timeSlots;

    public BookingDefenseSettingsDTO(String startDate, String endDate, String semesterYear, Map<String, List<TimeEachSlotDTO>> timeSlots) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.semesterYear = semesterYear;
        this.timeSlots = timeSlots;
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

    public Map<String, List<TimeEachSlotDTO>> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(Map<String, List<TimeEachSlotDTO>> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public String getSemesterYear() {
        return semesterYear;
    }

    public void setSemesterYear(String semesterYear) {
        this.semesterYear = semesterYear;
    }
}