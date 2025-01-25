package com.example.project.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "DefenseSchedule")
public class DefenseSchedule {

    @Id
    @Column(name = "defense_schedule_id")
    private String defenseScheduleId;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "date")
    private String date;

    @Column(name = "status")
    private String status;

    @Column(name = "remark")
    private String remark;

    @Column(name = "edited_on")
    private LocalDateTime editedOn;

    @ManyToOne
    @JoinColumn(name = "edited_by", referencedColumnName = "user_username")
    private Account editedBy;

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "project_id")
    private Project projectId;

    @ManyToOne
    @JoinColumn(name = "room_number", referencedColumnName = "room_number")
    private Room roomNumber;

    public String getDefenseScheduleId() {
        return defenseScheduleId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public String getRemark() {
        return remark;
    }

    public LocalDateTime getEditedOn() {
        return editedOn;
    }

    public Account getEditedBy() {
        return editedBy;
    }

    public Project getProjectId() {
        return projectId;
    }

    public Room getRoomNumber() {
        return roomNumber;
    }
}
