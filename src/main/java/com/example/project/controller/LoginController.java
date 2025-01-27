package com.example.project.controller;

import com.example.project.entity.Account;
import com.example.project.entity.Admin;
import com.example.project.entity.Instructor;
import com.example.project.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
//@RestController
public class LoginController {

    @GetMapping("/")
    @ResponseBody
    public String defaultPage() {
        return ("Hello World");
    }

    // login page (USE)
    @GetMapping("/login")
    public String logInPage() {
        return "Login";
    }


    // account path
    private final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    // use login
    // https://www.codejava.net/frameworks/spring/spring-redirectview-and-redirectattributes-examples

    @PostMapping("/login")
    public String userLogin(@RequestParam String username, @RequestParam String password, RedirectAttributes redirectAttributes){
        if(loginService.authentication(username, password)) {
            String role = loginService.getAccountRole(username);
            if (role.equals("Admin")) {
                return "AdminHome";
            } else if (role.equals("Instructor")) {
                return "InstructorHome";
            } else {
                redirectAttributes.addFlashAttribute("error", "Invalid user role");
                return "redirect:/login";
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid username or password");
            return "redirect:/login";
        }
    }


    // account
    @GetMapping("/account/all")
    public List<Account> getAllAccount(){
        return loginService.getAllAccount();
    }

    @GetMapping("/account/admin")
    public List<Account> getAdminAccount() {
        return loginService.getAdminAccount();
    }

    @GetMapping("/account/instructor")
    public List<Account> getInstructorAccount() {
        return loginService.getInstructorAccount();
    }

    // admin
    @GetMapping("/admin")
    public List<Admin> getAdmin() {
        return loginService.getAdmin();
    }

    // instructor
    @GetMapping("/instructor")
    public List<Instructor> getInstructor() {
        return loginService.getInstructor();
    }
}
