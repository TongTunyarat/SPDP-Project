package com.example.project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "Account")
public class Account {

    @Id
    @Column(name = "user_username")
    private String username;

    @Column(name = "user_password")
    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

