package com.example.project.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    // user_name
    @OneToOne
    @JoinColumn(name = "user_username")
    @JsonManagedReference
    private Account adminsAccount;

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
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

    public String getAdminType() {
        return adminType;
    }

    public void setAdminType(String adminType) {
        this.adminType = adminType;
    }

    public Account getAdminsAccount() {
        return adminsAccount;
    }

    public void setAdminsAccount(Account adminsAccount) {
        this.adminsAccount = adminsAccount;
    }

}
