package com.example.project.DTO.ManageSchedule.Preview;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class PreviewProposalDTO {

    private String projectId;
    private String projectTitle;
    private List<StudentDataDTO> students;
    private String program;
    private String semester;
    private Map<String, List<String>> instructorNames;
    private String date;
    private String Time;
    private String room;
    private String status;
    private String editedByUser;

    public PreviewProposalDTO(String projectId, String projectTitle, List<StudentDataDTO> students, String program, String semester, Map<String, List<String>> instructorNames, String date, String time, String room, String status, String editedByUser) {
        this.projectId = projectId;
        this.projectTitle = projectTitle;
        this.students = students;
        this.program = program;
        this.semester = semester;
        this.instructorNames = instructorNames;
        this.date = date;
        Time = time;
        this.room = room;
        this.status = status;
        this.editedByUser = editedByUser;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public List<StudentDataDTO> getStudents() {
        return students;
    }

    public void setStudents(List<StudentDataDTO> students) {
        this.students = students;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public Map<String, List<String>> getInstructorNames() {
        return instructorNames;
    }

    public void setInstructorNames(Map<String, List<String>> instructorNames) {
        this.instructorNames = instructorNames;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEditedByUser() {
        return editedByUser;
    }

    public void setEditedByUser(String editedByUser) {
        this.editedByUser = editedByUser;
    }

    @Override
    public String toString() {
        return "PreviewProposalDTO{" +
                "projectId='" + projectId + '\'' +
                ", projectTitle='" + projectTitle + '\'' +
                ", students=" + students +
                ", program='" + program + '\'' +
                ", semester='" + semester + '\'' +
                ", instructorNames=" + instructorNames +
                ", date='" + date + '\'' +
                ", Time='" + Time + '\'' +
                ", room='" + room + '\'' +
                ", status='" + status + '\'' +
                ", editedByUser='" + editedByUser + '\'' +
                '}';
    }
}