package com.example.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "proposalschedule")
public class ProposalSchedule {

    @Id
    @Column(name = "proposal_schedule_id")
    private String proposalScheduleId;

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

    @Column(name = "record_on")
    private LocalDateTime recordOn;

    @Column(name = "edited_on")
    private LocalDateTime editedOn;

    // edited_by
    @ManyToOne
    @JoinColumn(name = "edited_by", insertable = false, updatable = false)
    @JsonManagedReference
    private Account editedBy;

    @Column(name = "edited_by")
    private String editedByUser;

    // project_id
    @ManyToOne
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    @JsonManagedReference
    private Project project;

    @Column(name = "project_id")
    private String projectId;

    // room_number
    @ManyToOne
    @JoinColumn(name = "room_number", insertable = false, updatable = false)
    @JsonManagedReference
    private Room roomNumber;

    @Column(name = "room_number")
    private String room;

    public String getProposalScheduleId() {
        return proposalScheduleId;
    }

    public void setProposalScheduleId(String proposalScheduleId) {
        this.proposalScheduleId = proposalScheduleId;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getEditedOn() {
        return editedOn;
    }

    public void setEditedOn(LocalDateTime editedOn) {
        this.editedOn = editedOn;
    }

    public Account getEditedBy() {
        return editedBy;
    }

    public void setEditedBy(Account editedBy) {
        this.editedBy = editedBy;
    }

    public Room getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Room roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public LocalDateTime getRecordOn() {
        return recordOn;
    }

    public void setRecordOn(LocalDateTime recordOn) {
        this.recordOn = recordOn;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getEditedByUser() {
        return editedByUser;
    }

    public void setEditedByUser(String editedByUser) {
        this.editedByUser = editedByUser;
    }


}