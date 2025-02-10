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
                        .ignoringRequestMatchers("/user/details", "/testsave", "/saveEvaluation", "/getEvaluation", "/saveDefenseEvaluation", "/getDefenseEvaluation", "/savePosterEvaluation", "/getPosterEvaluation", "/scoreTotal", "/scoreTotalPoster")
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/css/**", "/js/**", "/publicProjectDetail", "/instructor/criteriaDefenseGrade", "/testsave", "/saveEvaluation", "/getEvaluation", "/saveDefenseEvaluation", "/getDefenseEvaluation", "/savePosterEvaluation", "/getPosterEvaluation", "/scoreTotal", "/scoreTotalPoster").permitAll() // อนุญาตให้ทุกคนเข้าถึง /testsave
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/instructor/**").hasRole("INSTRUCTOR")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler((request, response, authentication) -> {
                            Set<String> roles = authentication.getAuthorities().stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .collect(Collectors.toSet());
                            if(roles.contains("ROLE_ADMIN")){
                                response.sendRedirect("/admin/home");
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
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );
        return http.build();
    }


//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf( csrf ->  csrf
//                        .ignoringRequestMatchers("/user/details")
//                )
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/login", "/css/**", "/js/**", "/publicProjectDetail", "/instructor/criteriaDefenseGrade", "/testsave", "/save").permitAll()
//                        .requestMatchers("/admin/**").hasRole("ADMIN")
//                        .requestMatchers("/instructor/**").hasRole("INSTRUCTOR")
//                        .anyRequest().authenticated()
//                )
//                .formLogin(form -> form
//                        // user ที่จะเข้าระบบได้จะต้องผ่าน
//                        .loginPage("/login")
//                        .loginProcessingUrl("/login")
//                        .successHandler((request, response, authentication) -> {
//                            // request.getSession().removeAttribute("SPRING_SECURITY_LAST_EXCEPTION");
//                            // https://stackoverflow.com/questions/12612096/how-to-check-if-authority-exists-in-a-collection-of-grantedauthority
//                            Set<String> roles = authentication.getAuthorities().stream()
//                                    // ดึงค่า getAuthority() ของ SimpleGrantedAuthority
//                                    .map(GrantedAuthority::getAuthority)
//                                    .collect(Collectors.toSet());
//                            if(roles.contains("ROLE_ADMIN")){
//                                response.sendRedirect("/admin/home");
//                            } else if (roles.contains("ROLE_INSTRUCTOR")) {
//                                response.sendRedirect("/instructor/view");
//                            }
//                        })
//                        .failureHandler((request, response, authentication) -> {
//                            response.sendRedirect("/login?error=invalid_credentials");
//                        })
//                        .permitAll()
//                )
//                .logout(logout -> logout
//                        .logoutSuccessUrl("/login?logout")
//                        // delete current session
//                        .invalidateHttpSession(true)
//                        // when user login -> https://docs.spring.io/spring-security/reference/servlet/authentication/session-management.html
//                        .deleteCookies("JSESSIONID")
//                        .permitAll()
//                );
//        return http.build();
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return  new BCryptPasswordEncoder();
    }

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