package com.example.project.controller;

import com.example.project.entity.Account;
import com.example.project.entity.Admin;
import com.example.project.entity.Instructor;
import com.example.project.repository.AccountRepository;
import com.example.project.repository.AdminRepository;
import com.example.project.repository.InstructorRepository;
import com.example.project.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private TestService testService;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/")
    public String index() {
        return "Login";  // ชื่อของไฟล์ HTML (ไม่ต้องใส่นามสกุล .html)
    }

    @GetMapping("/admins/all")
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    @GetMapping("/instructor/all")
    public List<Instructor> getAllInstructors() {
        return instructorRepository.findAll();
    }

    @GetMapping("/accounts/all")
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @GetMapping("/periodSettings")
    public String getPeriodSettings() { return "ScorePeriodSettings"; }

}
