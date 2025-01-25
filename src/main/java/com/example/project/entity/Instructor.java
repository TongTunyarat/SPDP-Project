package com.example.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "instructor")
public class Instructor {

    @Id
    @Column(name = "professor_id")
    private String professorId;

    @Column(name = "professor_name")
    private String professorName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @OneToOne
    @JoinColumn(name = "user_username")
    @JsonManagedReference
    private Account account;

//    @Column(name = "user_username")
//    private String user_username;
    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<ProjectInstructorRole> projectInstructorRoles;


    public Instructor() {

    }

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }


    public List<ProjectInstructorRole> getProjectInstructorRoles() {
        return projectInstructorRoles;
    }

    public void setProjectInstructorRoles(List<ProjectInstructorRole> projectInstructorRoles) {
        this.projectInstructorRoles = projectInstructorRoles;
    }
}