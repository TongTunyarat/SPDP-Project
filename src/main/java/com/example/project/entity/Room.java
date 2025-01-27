package com.example.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Entity
@Table(name = "Room")
public class Room {

    @Id
    @Column(name = "room_number")
    private String roomNumber;

    @Column(name = "floor")
    private int floor;

    @Column(name = "capacity")
    private int capacity;

    // map to ProposalSchedule ('room_number')
    @OneToMany(mappedBy = "roomNumber", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<ProposalSchedule> proposalSchedules;

    // map to DefenseSchedule ('room_number')
    @OneToMany(mappedBy = "roomNumber", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<DefenseSchedule> defenseSchedules;

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<ProposalSchedule> getProposalSchedules() {
        return proposalSchedules;
    }

    public void setProposalSchedules(List<ProposalSchedule> proposalSchedules) {
        this.proposalSchedules = proposalSchedules;
    }
}
