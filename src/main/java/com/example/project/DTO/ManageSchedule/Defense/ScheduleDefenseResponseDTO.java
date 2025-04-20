package com.example.project.DTO.ManageSchedule.Defense;

import com.example.project.DTO.ManageSchedule.ProjectWithInstructorsDTO;
import com.example.project.DTO.ManageSchedule.ScheduleAssignmentDTO;
import com.example.project.DTO.ManageSchedule.TimeSlotDTO;

import java.util.List;

public class ScheduleDefenseResponseDTO {

    private String status;
    private String message;
    private List<ScheduleAssignmentDTO> scheduledAssignments;
    private List<ProjectWithInstructorsDTO> unscheduledProjects;
    private List<String> RoomName;


    public ScheduleDefenseResponseDTO(String status, String message) {
        this.status = status;
        this.message = message;
    }

//    public ScheduleDefenseResponseDTO(String status, String message, List<ScheduleAssignmentDTO> scheduledAssignments, List<String> roomName) {
//        this.status = status;
//        this.message = message;
//        this.scheduledAssignments = scheduledAssignments;
//        RoomName = roomName;
//    }


    public ScheduleDefenseResponseDTO(String status, String message, List<ScheduleAssignmentDTO> scheduledAssignments, List<ProjectWithInstructorsDTO> unscheduledProjects, List<String> roomName) {
        this.status = status;
        this.message = message;
        this.scheduledAssignments = scheduledAssignments;
        this.unscheduledProjects = unscheduledProjects;
        RoomName = roomName;
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

    public List<String> getRoomName() {
        return RoomName;
    }

    public void setRoomName(List<String> roomName) {
        RoomName = roomName;
    }

    public List<ProjectWithInstructorsDTO> getUnscheduledProjects() {
        return unscheduledProjects;
    }

    public void setUnscheduledProjects(List<ProjectWithInstructorsDTO> unscheduledProjects) {
        this.unscheduledProjects = unscheduledProjects;
    }
}