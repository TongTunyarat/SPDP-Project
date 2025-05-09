package com.example.project.DTO.projectManagement;

import java.util.List;

public class NewProjectDTO {

    private String projectTitle;
    private String projectDescription;
    private String program;
    private String semester;
    private String projectCategory;
    private List<StudentProjectDTO> studentList;
    private List<ProfessorRoleDTO> professorList;


    public NewProjectDTO(String projectTitle, String projectDescription, String program, String semester, String projectCategory, List<StudentProjectDTO> studentList, List<ProfessorRoleDTO> professorList) {
        this.projectTitle = projectTitle;
        this.projectDescription = projectDescription;
        this.program = program;
        this.semester = semester;
        this.projectCategory = projectCategory;
        this.studentList = studentList;
        this.professorList = professorList;
    }

    public String getProjectCategory() {
        return projectCategory;
    }

    public void setProjectCategory(String projectCategory) {
        this.projectCategory = projectCategory;
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

    public List<StudentProjectDTO> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<StudentProjectDTO> studentList) {
        this.studentList = studentList;
    }

    public List<ProfessorRoleDTO> getProfessorList() {
        return professorList;
    }

    public void setProfessorList(List<ProfessorRoleDTO> professorList) {
        this.professorList = professorList;
    }
}
