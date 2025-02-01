package com.example.project.service;

import com.example.project.entity.Project;
import com.example.project.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShowProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public List<Project> findAllProject() {
        return projectRepository.findAll();
    }

    public List<Project> processWord(String word) {
        System.out.println("Search Word [Service]: " + word);
        return projectRepository.searchProjects(word);
    }
}
