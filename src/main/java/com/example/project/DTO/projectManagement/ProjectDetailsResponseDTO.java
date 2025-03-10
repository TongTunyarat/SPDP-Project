package com.example.project.DTO.projectManagement;

import java.util.List;

public class ProjectDetailsResponseDTO {
    private String projectId;
    private String projectTitle;
    private List<ProfessorRoleDTO> professorList;  // สำหรับเก็บข้อมูลอาจารย์ทั้งหมด
    private String projectDescription;
    private String program;
    private List<StudentProjectDTO> studentList;
    private List<ProfessorRoleDTO> advisors;  // สำหรับเก็บข้อมูล Advisor
    private List<ProfessorRoleDTO> coAdvisors;  // สำหรับเก็บข้อมูล Co-Advisor
    private List<ProfessorRoleDTO> committees;  // สำหรับเก็บข้อมูล Committee

    // Constructor
    public ProjectDetailsResponseDTO(String projectId, String projectTitle, List<ProfessorRoleDTO> professorList,
                             String projectDescription, String program, List<StudentProjectDTO> studentList,
                             List<ProfessorRoleDTO> advisors, List<ProfessorRoleDTO> coAdvisors,
                             List<ProfessorRoleDTO> committees) {
        this.projectId = projectId;
        this.projectTitle = projectTitle;
        this.professorList = professorList;
        this.projectDescription = projectDescription;
        this.program = program;
        this.studentList = studentList;
        this.advisors = advisors;
        this.coAdvisors = coAdvisors;
        this.committees = committees;
    }

    // Getter และ Setter
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

    public List<ProfessorRoleDTO> getProfessorList() {
        return professorList;
    }

    public void setProfessorList(List<ProfessorRoleDTO> professorList) {
        this.professorList = professorList;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public List<StudentProjectDTO> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<StudentProjectDTO> studentList) {
        this.studentList = studentList;
    }

    public List<ProfessorRoleDTO> getAdvisors() {
        return advisors;
    }

    public void setAdvisors(List<ProfessorRoleDTO> advisors) {
        this.advisors = advisors;
    }

    public List<ProfessorRoleDTO> getCoAdvisors() {
        return coAdvisors;
    }

    public void setCoAdvisors(List<ProfessorRoleDTO> coAdvisors) {
        this.coAdvisors = coAdvisors;
    }

    public List<ProfessorRoleDTO> getCommittees() {
        return committees;
    }

    public void setCommittees(List<ProfessorRoleDTO> committees) {
        this.committees = committees;
    }
}


