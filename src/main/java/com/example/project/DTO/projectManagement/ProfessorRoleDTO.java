package com.example.project.DTO.projectManagement;

import com.example.project.entity.Instructor;

public class ProfessorRoleDTO {
    private String professorId;
    private String professorName;
    private String role;

    // Constructor
    public ProfessorRoleDTO(String professorName, String role, String professorId) {
        this.professorName = professorName;
        this.role = role;
        this.professorId = professorId;
    }

    // Getter และ Setter


    public String getProfessorId() {
        return professorId;
    }

    public void setProfessorId(String professorId) {
        this.professorId = professorId;
    }

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


}
