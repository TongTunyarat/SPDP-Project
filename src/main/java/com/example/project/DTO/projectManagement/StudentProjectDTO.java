package com.example.project.DTO.projectManagement;

public class StudentProjectDTO {
    private String studentId;
    private String studentName;
    private String section;
    private String track;
    private String status;


    public StudentProjectDTO(String studentId, String studentName, String section, String track, String status) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.section = section;
        this.track = track;
        this.status = status;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
