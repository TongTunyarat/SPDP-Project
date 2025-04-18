package com.example.project.DTO.ManageSchedule;

import java.time.LocalTime;
import java.util.List;

public class GetProposalUnScheduleDTO {

    private String proposalScheduleId;
    private String remark;
    private String status;
    private String projectId;
    private String program;
    private String semester;
    private String projectTitle;
    private List<String> instructors;

    public GetProposalUnScheduleDTO(String proposalScheduleId, String remark, String status, String projectId, String program, String semester, String projectTitle, List<String> instructors) {
        this.proposalScheduleId = proposalScheduleId;
        this.remark = remark;
        this.status = status;
        this.projectId = projectId;
        this.program = program;
        this.semester = semester;
        this.projectTitle = projectTitle;
        this.instructors = instructors;
    }

    public String getProposalScheduleId() {
        return proposalScheduleId;
    }

    public void setProposalScheduleId(String proposalScheduleId) {
        this.proposalScheduleId = proposalScheduleId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
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

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public List<String> getInstructors() {
        return instructors;
    }

    public void setInstructors(List<String> instructors) {
        this.instructors = instructors;
    }
}