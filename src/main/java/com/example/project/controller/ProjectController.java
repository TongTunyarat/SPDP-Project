package com.example.project.controller;

import com.example.project.entity.Project;
import com.example.project.repository.ProjectRepository;
import com.example.project.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//@RestController
@Controller
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping("/projects")
    public List<String> getAllProjectNames() {
        return projectService.getAllProjectNames();
    }

    @GetMapping("/projects/all")
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

    @GetMapping("/")
    public String index() {
        return "Login";  // ชื่อของไฟล์ HTML (ไม่ต้องใส่นามสกุล .html)
    }

    @GetMapping("/proposal-evaluation")
    public String getProposalEvaluation(Model model) {
        List<Project> projects = projectRepository.findAll();  // Assuming you're using a repository for data access
        model.addAttribute("projects", projects);
        return "ProposalEva";  // Name of your Thymeleaf template
    }

    @GetMapping("/periodSettings")
    public String getPeriodSettings() { return "ScorePeriodSettings"; }


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
