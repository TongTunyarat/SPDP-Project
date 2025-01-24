package com.example.project.service;

import com.example.project.entity.Project;
import com.example.project.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

//    public List<String> getAllProjectNames() {
//        List<Project> projects = projectRepository.findAll();
//        return projects.stream()
//                .map(Project::getProjectTitle)
//                .collect(Collectors.toList());
//    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }
}
