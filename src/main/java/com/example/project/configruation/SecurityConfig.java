package com.example.project.configruation;

import com.example.project.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


import java.util.Set;
import java.util.stream.Collectors;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private LoginService loginService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/user/details", "/testsave", "/saveEvaluation", "/getEvaluation", "/saveDefenseEvaluation", "/getDefenseEvaluation", "/savePosterEvaluation", "/getPosterEvaluation", "/scoreTotal", "/scoreTotalPoster", "/saveProposalGrade","/getGradeProposal", "/saveDefenseGrade", "/getGradeDefense", "/instructor/showGradeScoreDefense", "/admin/projectOverview", "/admin/uploadProjectFiles", "/admin/editDetails", "/admin/updateProjectDetails", "/admin/deleteProject/**","/admin/deleteAllProjects","/admin/deleteProjectsBySemester", "/admin/deleteStudentFromProject/**", "/admin/deleteInstructorFromProject/**", "/admin/addProject", "/admin/editDetails", "/get-instructorId", "/api/proposal-evaluation", "/api/defense-evaluation", "/scoring-periods", "/admin/previewFiles", "/admin/uploadCommitteeFiles", "/admin/uploadPosterCommitteeFiles","/admin/validateProjectFiles", "/admin/uploadProjectDBFiles","/admin/deleteCommittee")
                        .ignoringRequestMatchers("/user/details", "/testsave", "/saveEvaluation", "/getEvaluation", "/saveDefenseEvaluation", "/getDefenseEvaluation", "/savePosterEvaluation", "/getPosterEvaluation", "/scoreTotal", "/scoreTotalPoster", "/saveProposalGrade","/getGradeProposal", "/saveDefenseGrade", "/getGradeDefense", "/instructor/showGradeScoreDefense", "/admin/projectOverview", "/admin/uploadFiles", "/admin/editDetails", "/admin/updateProjectDetails", "/admin/deleteProject/**", "/admin/deleteStudentFromProject/**", "/admin/addNewProject", "/admin/editDetails", "/api/save/timeliness", "/get-instructorId", "/api/proposal-evaluation", "/api/defense-evaluation", "/scoring-periods","/admin/deleteCommittee")
                
                     )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/css/**", "/js/**", "/publicProjectDetail","/student", "/instructor/criteriaDefenseGrade", "/testsave", "/saveEvaluation", "/getEvaluation", "/saveDefenseEvaluation", "/getDefenseEvaluation", "/savePosterEvaluation", "/getPosterEvaluation", "/scoreTotal", "/scoreTotalPoster", "/saveProposalGrade", "/getGradeProposal", "/saveDefenseGrade", "/getGradeDefense", "/instructor/showGradeScoreDefense", "/get-instructorId", "/api/proposal-evaluation", "/api/defense-evaluation",  "/scoring-periods", "/admin/projectOverview", "/admin/uploadProjectFiles", "/admin/editDetails", "/admin/updateProjectDetails", "/admin/deleteProject/**","/admin/deleteAllProjects","/admin/deleteProjectsBySemester", "/admin/deleteStudentFromProject/**" , "/admin/deleteInstructorFromProject/**","/admin/addProject", "/admin/editDetails", "/admin/previewFiles", "/admin/uploadCommitteeFiles", "/admin/uploadPosterCommitteeFiles", "/admin/validateProjectFiles", "/admin/uploadProjectDBFiles").permitAll() // อนุญาตให้ทุกคนเข้าถึง
//                        "/publicProjectDetail", "/instructor/criteriaDefenseGrade", "/testsave", "/saveEvaluation", "/getEvaluation", "/saveDefenseEvaluation", "/getDefenseEvaluation", "/savePosterEvaluation", "/getPosterEvaluation", "/scoreTotal", "/scoreTotalPoster", "/saveProposalGrade", "/getGradeProposal", "/saveDefenseGrade", "/getGradeDefense", "/instructor/showGradeScoreDefense"
     
                        .requestMatchers("/login", "/css/**", "/js/**", "/publicProjectDetail","/student", "/instructor/criteriaDefenseGrade", "/testsave", "/saveEvaluation", "/getEvaluation", "/saveDefenseEvaluation", "/getDefenseEvaluation", "/savePosterEvaluation", "/getPosterEvaluation", "/scoreTotal", "/scoreTotalPoster", "/saveProposalGrade", "/getGradeProposal", "/saveDefenseGrade", "/getGradeDefense", "/instructor/showGradeScoreDefense", "/get-instructorId", "/api/proposal-evaluation", "/api/defense-evaluation",  "/scoring-periods", "/api/save/timeliness", "/admin/projectOverview", "/admin/uploadFiles", "/admin/editDetails", "/admin/updateProjectDetails", "/admin/deleteProject/**", "/admin/deleteStudentFromProject/**" ,"/admin/addNewProject", "/admin/editDetails").permitAll() // อนุญาตให้ทุกคนเข้าถึง

                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/instructor/**").hasRole("INSTRUCTOR")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login") // url ใน post method
                        .successHandler((request, response, authentication) -> {
                            //[SimpleGrantedAuthority("ROLE_ADMIN"), SimpleGrantedAuthority("ROLE_INSTRUCTOR")]
                            Set<String> roles = authentication.getAuthorities().stream()
                                    // string role
                                    .map(GrantedAuthority::getAuthority)
                                    .collect(Collectors.toSet());
                            if(roles.contains("ROLE_ADMIN")){
                                response.sendRedirect("/admin-dashboard");
//                                  response.sendRedirect("/admin/editProposalSchedulePage");
                            } else if (roles.contains("ROLE_INSTRUCTOR")) {
                                response.sendRedirect("/instructor/view");
                            }
                        })
                        .failureHandler((request, response, authentication) -> {
                            response.sendRedirect("/login?error=invalid_credentials");
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true) // delete
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return  new BCryptPasswordEncoder();
    }

    // $2a$10$qYbZSUa3B/rJX4M1bX5keuQ5GcvFAeqnUJ2Y3iZT9Vc9EVJ9h1F9G
    // $2a$ คือ version ของ BCrypt
    // 10$ คือ work factor
    // qYbZSUa3B/rJX4M1bX5keu คือ salt
    // hashed password

    // https://stackoverflow.com/questions/77504542/rewriting-a-spring-security-deprecated-authenticationmanager-httpsecurity
    // จัดการ authentication
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http)  throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(loginService)
                .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }
}