package com.example.project.DTO.projectManagement;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ExcelDataDTO {

    @ExcelProperty("Project ID")
    private String projectId;

    @ExcelProperty("Project Title")
    private String projectTitle;

    @ExcelProperty("Project Description")
    private String projectDescription;

    @ExcelProperty("Advisor")
    private String advisor;

    @ExcelProperty("Committee")
    private String committee;

    @ExcelProperty("Poster Committee")
    private String posterCommittee;

    @ExcelProperty("Student ID")
    private String studentId;

    @ExcelProperty("Student Name")
    private String studentName;

    @ExcelProperty("Program")
    private String program;

    @ExcelProperty("Section")
    private String section;

    @ExcelProperty("Track")
    private String track;

    public ExcelDataDTO() {
    }

    public ExcelDataDTO(String projectId, String projectTitle, String projectDescription, String advisor, String committee, String posterCommittee, String studentId, String studentName, String program, String section, String track) {
        this.projectId = projectId;
        this.projectTitle = projectTitle;
        this.projectDescription = projectDescription;
        this.advisor = advisor;
        this.committee = committee;
        this.posterCommittee = posterCommittee;
        this.studentId = studentId;
        this.studentName = studentName;
        this.program = program;
        this.section = section;
        this.track = track;
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
