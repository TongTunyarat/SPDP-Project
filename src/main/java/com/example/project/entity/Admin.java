package com.example.project.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "Admin")
public class Admin {

    @Id
    @Column(name = "admin_id")
    private String adminId;

    @Column(name = "admin_name")
    private String adminName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "admin_type")
    private String adminType;

    @ManyToOne
    @JoinColumn(name = "user_username", referencedColumnName = "user_username")
    private Account user_username;

    public String getAdminId() {
        return adminId;
    }

    public String getAdminName() {
        return adminName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAdminType() {
        return adminType;
    }

    public Account getUser_username() {
        return user_username;
    }
}
