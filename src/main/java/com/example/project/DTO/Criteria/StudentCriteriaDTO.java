package com.example.project.DTO.Criteria;

public class StudentCriteriaDTO {
    private String studentPjId;
    private String studentId;
    private String studentName;
    private String program;
    private int section;
    private String track;
    private String email;
    private String phone;
    private String projectId;
    private String projectTitle;
    private String status;

    public StudentCriteriaDTO(String studentPjId, String studentId, String studentName, String program, int section, String track, String email, String phone, String projectId, String projectTitle, String status) {
        this.studentPjId = studentPjId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.program = program;
        this.section = section;
        this.track = track;
        this.email = email;
        this.phone = phone;
        this.projectId = projectId;
        this.projectTitle = projectTitle;
        this.status = status;
    }

    public String getStudentPjId() {
        return studentPjId;
    }

    public void setStudentPjId(String studentPjId) {
        this.studentPjId = studentPjId;
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

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}