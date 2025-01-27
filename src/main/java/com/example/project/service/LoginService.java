package com.example.project.service;

import com.example.project.entity.Account;
import com.example.project.entity.Admin;
import com.example.project.entity.Instructor;
import com.example.project.repository.AccountRepository;
import com.example.project.repository.AdminRepository;
import com.example.project.repository.InstructorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LoginService {

    private final AccountRepository accountRepository;
    private final AdminRepository adminRepository;

    private final InstructorRepository instructorRepository;

    @Autowired
    public LoginService(AccountRepository accountRepository, AdminRepository adminRepository, InstructorRepository instructorRepository) {
        this.accountRepository = accountRepository;
        this.adminRepository = adminRepository;
        this.instructorRepository = instructorRepository;

    }

    // find account
    public List<Account> getAllAccount() {
        return accountRepository.findAll();
    }

    public boolean authentication(String username, String password) {
        Optional<Account> account = accountRepository.findAccountByUsername(username);
        return account.map(i -> i.getPassword().equals(password)).orElse(false);
    }

    // get role
    public String getAccountRole(String username) {
        Optional<Account> account = accountRepository.findAccountByUsername(username);
        if (account.isPresent()) {
            if(account.get().getAdmins() != null) {
                return "Admin";
            } else if (account.get().getInstructors() != null) {
                return "Instructor";
            }
        }
        return "Unknow";
    }

    // find account each role
    public List<Account> getAdminAccount() {
        return accountRepository.findAll().stream()
                .filter(account -> account.getAdmins() != null)
                .collect(Collectors.toList());
    }

    public List<Account> getInstructorAccount() {
        return accountRepository.findAll().stream()
                .filter(account -> account.getInstructors() != null)
                .collect(Collectors.toList());
    }



    // admin
    public List<Admin> getAdmin() {
        return adminRepository.findAll();
    }

    // instructor
    public List<Instructor> getInstructor() {
        return instructorRepository.findAll();
    }

}
