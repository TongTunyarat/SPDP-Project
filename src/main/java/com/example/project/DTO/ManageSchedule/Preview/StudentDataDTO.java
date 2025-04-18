package com.example.project.DTO.ManageSchedule.Preview;

public class StudentDataDTO {
    private String studentId;
    private String studentName;
    private Byte section;
    private String track;

    public StudentDataDTO(String studentId, String studentName, Byte section, String track) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.section = section;
        this.track = track;
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

    public Byte getSection() {
        return section;
    }

    public void setSection(Byte section) {
        this.section = section;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    @Override
    public String toString() {
        return "StudentDataDTO{" +
                "studentId='" + studentId + '\'' +
                ", studentName='" + studentName + '\'' +
                ", section=" + section +
                ", track=" + track +
                '}';
    }
}