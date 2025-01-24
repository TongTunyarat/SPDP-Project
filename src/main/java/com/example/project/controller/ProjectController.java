package com.example.project.controller;

import com.example.project.entity.Admin;
import com.example.project.entity.Project;
import com.example.project.repository.AdminRepository;
import com.example.project.repository.ProjectRepository;
import com.example.project.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//@RestController
@RestController
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private AdminRepository adminRepository;

    @GetMapping("/projects/all")
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

    @GetMapping("/admins/all")
    public List<Admin> getAllAdmins() {
        System.out.println(adminRepository.findAll());
        return adminRepository.findAll();
    }

//    @GetMapping("/")
//    public String index() {
//        return "ProposalEva";
//    }
//
//    @GetMapping("/proposal-evaluation")
//    public String getProposalEvaluation(Model model) {
//        List<Project> projects = projectRepository.findAll();
//        model.addAttribute("projects", projects);
//        return "ProposalEva";
//    }

//    @GetMapping("/admin/all")
//    public List<Project> getAllAdmins() {
//        return projectService.getAllProjects();
//    }

//    @GetMapping("/projects")
//    public List<Project> getAllProject() {
//        List<Project> projects = projectService.getAllProjects();
//        System.out.println("Start Controller");
//        System.out.println(projects);
//        System.out.println("End Controller");
//        return projects;
//    }
//
//    @GetMapping("/project-all")
//    public ResponseEntity<List<Project>> getAllProjects() {
////        List<Project> projects = projectService.getAllProjects();
//        System.out.println(projectService.getAllProjects());
//        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(projectService.getAllProjects());
//    }

}
