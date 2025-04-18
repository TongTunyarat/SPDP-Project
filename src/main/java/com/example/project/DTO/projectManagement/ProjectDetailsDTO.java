package com.example.project.DTO.projectManagement;

import com.example.project.DTO.projectManagement.ProfessorRoleDTO;
import com.example.project.DTO.projectManagement.StudentProjectDTO;
import java.util.List;

public class ProjectDetailsDTO {
    private String projectId;
    private String projectTitle;
    private List<ProfessorRoleDTO> professorList;  // เพิ่ม professorList เพื่อเก็บข้อมูลอาจารย์ทั้งหมด
    private String projectDescription;
    private String program;
    private List<StudentProjectDTO> studentList;
    private String category;
    private String semester;

    // Constructor
    public ProjectDetailsDTO(String projectId, String projectTitle, List<ProfessorRoleDTO> professorList,
                             String projectDescription, String program, List<StudentProjectDTO> studentList, String category, String semester) {
        this.projectId = projectId;
        this.projectTitle = projectTitle;
        this.professorList = professorList;  // กำหนดค่าให้กับ professorList
        this.projectDescription = projectDescription;
        this.program = program;
        this.studentList = studentList;
        this.category = category;
        this.semester = semester;
    }

    // Getter และ Setter


    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
}
