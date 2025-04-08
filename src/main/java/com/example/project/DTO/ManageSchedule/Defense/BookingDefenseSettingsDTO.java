package com.example.project.DTO.ManageSchedule.Defense;

import java.util.List;

public class BookingDefenseSettingsDTO {

    private String startDate;
    private String endDate;

    public BookingDefenseSettingsDTO(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
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
}
