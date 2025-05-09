package com.example.project.service;

import com.example.project.entity.Account;
import com.example.project.repository.AccountRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LoginService implements UserDetailsService {

    private final AccountRepository accountRepository;

    public LoginService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    //=========================================== USE ===================================================


    // authentication wit spring security
    // UsernamePasswordAuthenticationToken(username, password)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User account not found"));
        // GrantedAuthority - interface กำหนด role
        List<GrantedAuthority> authorities = new ArrayList<>();

        if(account.getAdmins() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else if (account.getInstructors() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_INSTRUCTOR"));
        }

        // ส่งให้ security
        return new User(account.getUsername(), account.getPassword(), authorities);
    }

}













//public class LoginService {

//    private final AccountRepository accountRepository;
//    private final AdminRepository adminRepository;
//
//    private final InstructorRepository instructorRepository;
//
//    @Autowired
//    public LoginService(AccountRepository accountRepository, AdminRepository adminRepository, InstructorRepository instructorRepository) {
//        this.accountRepository = accountRepository;
//        this.adminRepository = adminRepository;
//        this.instructorRepository = instructorRepository;
//
//    }
//
//    // find account
//    public List<Account> getAllAccount() {
//        return accountRepository.findAll();
//    }
//
//    public boolean authentication(String username, String password) {
//        Optional<Account> account = accountRepository.findAccountByUsername(username);
//        return account.map(i -> i.getPassword().equals(password)).orElse(false);
//    }
//
//    // get role
//    public String getAccountRole(String username) {
//        Optional<Account> account = accountRepository.findAccountByUsername(username);
//        if (account.isPresent()) {
//            if(account.get().getAdmins() != null) {
//                return "Admin";
//            } else if (account.get().getInstructors() != null) {
//                return "Instructor";
//            }
//        }
//        return "Unknow";
//    }
//
//    // find account each role
//    public List<Account> getAdminAccount() {
//        return accountRepository.findAll().stream()
//                .filter(account -> account.getAdmins() != null)
//                .collect(Collectors.toList());
//    }
//
//    public List<Account> getInstructorAccount() {
//        return accountRepository.findAll().stream()
//                .filter(account -> account.getInstructors() != null)
//                .collect(Collectors.toList());
//    }
//
//
//
//    // admin
//    public List<Admin> getAdmin() {
//        return adminRepository.findAll();
//    }
//
//    // instructor
//    public List<Instructor> getInstructor() {
//        return instructorRepository.findAll();
//    }

//}