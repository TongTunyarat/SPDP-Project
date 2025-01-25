package com.example.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Account")
public class Account {

    @Id
    @Column(name = "user_username")
    private String username;

    @Column(name = "user_password")
    private String password;

    // https://www.baeldung.com/jpa-one-to-one
    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    @JsonBackReference
    private Instructor instructors;

    @OneToOne(mappedBy = "adminsAccount", cascade = CascadeType.ALL)
    @JsonBackReference
    private Admin admins;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Instructor getInstructors() {
        return instructors;
    }

    public void setInstructors(Instructor instructors) {
        this.instructors = instructors;
    }

    public Admin getAdmins() {
        return admins;
    }

    public void setAdmins(Admin admins) {
        this.admins = admins;
    }
}

