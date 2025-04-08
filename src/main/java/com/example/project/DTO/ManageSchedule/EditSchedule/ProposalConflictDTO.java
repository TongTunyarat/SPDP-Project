package com.example.project.DTO.ManageSchedule.EditSchedule;

import java.util.List;

public class ProposalConflictDTO {

    private boolean hasErrorSaveData;
    private List<RoomColflictDTO> roomConflicts;
    private boolean hasRoomConflict;
    private List<InstructorConflictDTO> instructorConflicts;
    private boolean hasInstructorConflict;

    public ProposalConflictDTO(boolean hasErrorSaveData, List<RoomColflictDTO> roomConflicts, boolean hasRoomConflict, List<InstructorConflictDTO> instructorConflicts, boolean hasInstructorConflict) {
        this.hasErrorSaveData = hasErrorSaveData;
        this.roomConflicts = roomConflicts;
        this.hasRoomConflict = hasRoomConflict;
        this.instructorConflicts = instructorConflicts;
        this.hasInstructorConflict = hasInstructorConflict;
    }

    public boolean isHasErrorSaveData() {
        return hasErrorSaveData;
    }

    public void setHasErrorSaveData(boolean hasErrorSaveData) {
        this.hasErrorSaveData = hasErrorSaveData;
    }

    public List<RoomColflictDTO> getRoomConflicts() {
        return roomConflicts;
    }

    public void setRoomConflicts(List<RoomColflictDTO> roomConflicts) {
        this.roomConflicts = roomConflicts;
    }

    public boolean isHasRoomConflict() {
        return hasRoomConflict;
    }

    public void setHasRoomConflict(boolean hasRoomConflict) {
        this.hasRoomConflict = hasRoomConflict;
    }

    public List<InstructorConflictDTO> getInstructorConflicts() {
        return instructorConflicts;
    }

    public void setInstructorConflicts(List<InstructorConflictDTO> instructorConflicts) {
        this.instructorConflicts = instructorConflicts;
    }

    public boolean isHasInstructorConflict() {
        return hasInstructorConflict;
    }

    public void setHasInstructorConflict(boolean hasInstructorConflict) {
        this.hasInstructorConflict = hasInstructorConflict;
    }

    @Override
    public String toString() {
        return "ProposalConflictDTO{" +
                "hasErrorSaveData=" + hasErrorSaveData +
                ", roomConflicts=" + roomConflicts +
                ", hasRoomConflict=" + hasRoomConflict +
                ", instructorConflicts=" + instructorConflicts +
                ", hasInstructorConflict=" + hasInstructorConflict +
                '}';
    }
}
