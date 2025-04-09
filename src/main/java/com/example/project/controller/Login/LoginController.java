package com.example.project.controller.Login;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.context.request.RequestContextHolder;


@Controller
//@RestController
public class LoginController {

    @GetMapping("/")
    @ResponseBody
    public String defaultPage() {
        return ("Hello World");
    }


    //=========================================== USE ===================================================

    // login page throw any error
    @GetMapping("/login")
    // param จาก config
    public String loginPage(@RequestParam(required = false) String error, @RequestParam(required = false) String logout, Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }
        return "login";
    }



    // Prepare Admin login
    //https://medium.com/@CodeWithTech/understanding-securitycontext-and-securitycontextholder-in-spring-security-e8ec9c030819
    //https://stackoverflow.com/questions/3542026/retrieving-session-id-with-spring-security
//    @GetMapping("/admin/home")
//    public String defautlAdmin() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        System.out.println("Account username: " + authentication.getName());
//        System.out.println("Session ID: " + RequestContextHolder.currentRequestAttributes().getSessionId());
//
//        return "AdminHome";
//    }









    //=========================================== See Result (Not Use) ===================================================

//    // เก็บไว้ดู session
//    @GetMapping("/session")
//    @ResponseBody
//    public String checkSession() {
//        try {
//            String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
//            return "Session ID: " + sessionId;
//        } catch (IllegalStateException e) {
//            return "Session ID has been invalidated";
//        }
//    }

}



//
//    // account path
//    private final LoginService loginService;
//
//    @Autowired
//    public LoginController(LoginService loginService) {
//        this.loginService = loginService;
//    }
//
//    // use login
//    // https://www.codejava.net/frameworks/spring/spring-redirectview-and-redirectattributes-examples
//
//    @PostMapping("/login")
//    public String userLogin(@RequestParam String username, @RequestParam String password, RedirectAttributes redirectAttributes){
//        if(loginService.authentication(username, password)) {
//            String role = loginService.getAccountRole(username);
//            if (role.equals("Admin")) {
//                return "AdminHome";
//            } else if (role.equals("Instructor")) {
//                return "InstructorHome";
//            } else {
//                redirectAttributes.addFlashAttribute("error", "Invalid user role");
//                return "redirect:/login";
//            }
//        } else {
//            redirectAttributes.addFlashAttribute("error", "Invalid username or password");
//            return "redirect:/login";
//        }
//    }
//
//
//    // account
//    @GetMapping("/account/all")
//    public List<Account> getAllAccount(){
//        return loginService.getAllAccount();
//    }
//
//    @GetMapping("/account/admin")
//    public List<Account> getAdminAccount() {
//        return loginService.getAdminAccount();
//    }
//
//    @GetMapping("/account/instructor")
//    public List<Account> getInstructorAccount() {
//        return loginService.getInstructorAccount();
//    }
//
//    // admin
//    @GetMapping("/admin")
//    public List<Admin> getAdmin() {
//        return loginService.getAdmin();
//    }
//
//    // instructor
//    @GetMapping("/instructor")
//    public List<Instructor> getInstructor() {
//        return loginService.getInstructor();
//    }