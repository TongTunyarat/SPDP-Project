package com.example.project.DTO.projectManagement;

import com.example.project.entity.Instructor;

public class ProfessorRoleDTO {
    private String professorName;
    private String role;

    // Constructor
    public ProfessorRoleDTO(String professorName, String role) {
        this.professorName = professorName;
        this.role = role;
    }

    // Getter และ Setter
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
