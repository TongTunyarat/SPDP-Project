package com.example.project.DTO.ManageSchedule;
import java.util.*;

public class ScheduleProposalResponseDTO {

    private String status;
    private String message;
    private List<ScheduleAssignmentDTO> scheduledAssignments;
    private List<TimeSlotDTO> allTimeSlots;


    public ScheduleProposalResponseDTO(String status, String message, List<ScheduleAssignmentDTO> scheduledAssignments, List<TimeSlotDTO> allTimeSlots) {
        this.status = status;
        this.message = message;
        this.scheduledAssignments = scheduledAssignments;
        this.allTimeSlots = allTimeSlots;
    }

    public ScheduleProposalResponseDTO(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ScheduleAssignmentDTO> getScheduledAssignments() {
        return scheduledAssignments;
    }

    public void setScheduledAssignments(List<ScheduleAssignmentDTO> scheduledAssignments) {
        this.scheduledAssignments = scheduledAssignments;
    }

    public List<TimeSlotDTO> getAllTimeSlots() {
        return allTimeSlots;
    }

    public void setAllTimeSlots(List<TimeSlotDTO> allTimeSlots) {
        this.allTimeSlots = allTimeSlots;
    }
}
