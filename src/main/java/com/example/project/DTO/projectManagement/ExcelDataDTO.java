package com.example.project.DTO.projectManagement;

import lombok.Data;

@Data
public class ExcelDataDTO {
    private String projectID;
    private String projectTitle;
    private String projectDescription;
    private String advisor;
    private String committee;
    private String posterCommittee;
    private String studentID;
    private String studentName;
    private String program;
    private String section;
    private String track;

    public ExcelDataDTO(String projectID, String projectTitle, String projectDescription, String advisor, String committee, String posterCommittee, String studentID, String studentName, String program, String section, String track) {
        this.projectID = projectID;
        this.projectTitle = projectTitle;
        this.projectDescription = projectDescription;
        this.advisor = advisor;
        this.committee = committee;
        this.posterCommittee = posterCommittee;
        this.studentID = studentID;
        this.studentName = studentName;
        this.program = program;
        this.section = section;
        this.track = track;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public String getAdvisor() {
        return advisor;
    }

    public void setAdvisor(String advisor) {
        this.advisor = advisor;
    }

    public String getCommittee() {
        return committee;
    }

    public void setCommittee(String committee) {
        this.committee = committee;
    }

    public String getPosterCommittee() {
        return posterCommittee;
    }

    public void setPosterCommittee(String posterCommittee) {
        this.posterCommittee = posterCommittee;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
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
}


